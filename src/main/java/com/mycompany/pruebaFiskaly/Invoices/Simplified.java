package com.mycompany.pruebaFiskaly.Invoices;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Clients;
import com.mycompany.pruebaFiskaly.Config;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Simplified {

    public static void createSimplifiedInvoice(int invoiceNumber, List<Map<String, String>> itemsList) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = String.valueOf(invoiceNumber);
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);
            
            JSONArray items = new JSONArray();
            double totalAmount = 0.0;

            for (Map<String, String> itemData : itemsList) {
                String text = itemData.get("text");
                String quantity = itemData.get("quantity");
                String unitAmount = itemData.get("unit_amount");
                String ivaRate = itemData.get("iva_rate");

                double unit = Double.parseDouble(unitAmount);
                double qty = Double.parseDouble(quantity);
                double iva = Double.parseDouble(ivaRate);
                double full = unit * qty * (1 + iva / 100);
                totalAmount += full;
                String fullAmount = String.format(Locale.US, "%.2f", full);

                JSONObject category = new JSONObject();
                category.put("type", "VAT");
                category.put("rate", ivaRate);

                JSONObject system = new JSONObject();
                system.put("type", "REGULAR");
                system.put("category", category);

                JSONObject item = new JSONObject();
                item.put("text", text);
                item.put("quantity", quantity);
                item.put("unit_amount", unitAmount);
                item.put("full_amount", fullAmount);
                item.put("system", system);

                items.put(item);
            }

            String fullAmountTotal = String.format(Locale.US, "%.2f", totalAmount);

            JSONObject content = new JSONObject();
            content.put("type", "SIMPLIFIED");
            content.put("number", invoice_number);
            content.put("text", "Factura simplificada");
            content.put("full_amount", fullAmountTotal);
            content.put("items", items);

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);
            JSONObject jsonPrint = new JSONObject(responseBody);
            System.out.println(jsonPrint.toString(2));

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject json = new JSONObject(responseBody).getJSONObject("content");
                JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
                String qrBase64 = qr.getString("data");

                generateSimplifiedInvoicePDF(invoice_number, itemsList, fullAmountTotal, qrBase64);
            } else {
                System.err.println("Error al crear la factura simplificada (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.out.println("Error al crear la factura");
            e.printStackTrace();
        }
    }

    public static void createItem(List<Map<String, String>> itemsList, String text, String quantity, String unit_amount, String iva_rate) {
        //Validar que el IVA está entre los permitidos
        List<String> validIvaRates = Arrays.asList(Config.IVA_GENERAL, Config.IVA_REDUCIDO, Config.IVA_SUPERREDUCIDO, Config.IVA_EXENTO);
        if (!validIvaRates.contains(iva_rate)) {
            throw new IllegalArgumentException("IVA no válido: " + iva_rate + ". Debe ser uno de: " + validIvaRates);
        }
        Map<String, String> item = new HashMap<>();
        item.put("text", text);
        item.put("quantity", quantity);
        item.put("unit_amount", unit_amount);
        item.put("iva_rate", iva_rate);
        itemsList.add(item);
    }

    public static void generateSimplifiedInvoicePDF(String number, List<Map<String, String>> itemsData, String fullAmount, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura " + number + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA SIMPLIFICADA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Número: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(Chunk.NEWLINE);

        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<Map<String, String>>> groupedByIVA = new HashMap<>();
        for (Map<String, String> item : itemsData) {
            String ivaRaw = item.getOrDefault("iva_rate", "21.0").trim();
            String ivaRate = String.format(Locale.US, "%.1f", Double.parseDouble(ivaRaw));
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21.0", "10.0", "4.0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<Map<String, String>> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);

            Stream.of("Descripción", "Precio unitario", "Unidades", "Base imponible")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                        header.setBorderWidth(1);
                        table.addCell(header);
                    });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (Map<String, String> item : group) {
                String text = item.get("text");
                String unitAmount = item.get("unit_amount");
                String quantity = item.get("quantity");

                double unit = Double.parseDouble(unitAmount);
                double qty = Double.parseDouble(quantity);
                double base = unit * qty;
                double iva = Double.parseDouble(ivaRate);
                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(text);
                table.addCell(String.format(Locale.US, "%.2f €", unit));
                table.addCell(quantity);
                table.addCell(String.format(Locale.US, "%.2f €", base));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph("Total base: " + String.format(Locale.US, "%.2f €", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f €", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TOTAL GENERAL ========
        Paragraph total = new Paragraph("Importe total (IVA incluido): "
                + String.format(Locale.US, "%.2f €", totalConIVA),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        document.add(Chunk.NEWLINE);

        // ======== QR FISCAL ========
        byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.scaleToFit(120, 120);
        qrImage.setAlignment(Image.ALIGN_RIGHT);
        document.add(new Paragraph("\nCódigo QR fiscal:"));
        document.add(qrImage);

        // ======== PIE DE PÁGINA ========
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Factura emitida electrónicamente conforme a la normativa Verifactu.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));
        document.add(new Paragraph("Gracias por su confianza.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
        System.out.println("PDF guardado en: " + desktopPath + fileName);
    }

}

package com.mycompany.pruebaFiskaly.Invoices;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Clients;
import com.mycompany.pruebaFiskaly.Config;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

public class Complete {

    // FACTURA COMPLETA
    public static void createCompleteInvoice(int numFactura, List<Map<String, String>> itemsList, Map<String, String> receptorDetails) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = String.valueOf(numFactura);
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
                String discount = itemData.get("discount");
                String ivaRate = itemData.get("iva_rate");

                double unit = Double.parseDouble(unitAmount);
                double qty = Double.parseDouble(quantity);
                double dct = Double.parseDouble(discount);
                double iva = Double.parseDouble(ivaRate);
                double full = ((unit * qty) - dct) * (1 + iva / 100);
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
                item.put("discount", discount);
                item.put("full_amount", fullAmount);
                item.put("system", system);

                items.put(item);
            }

            String fullAmountTotal = String.format(Locale.US, "%.2f", totalAmount);

            // ======== RECEPTOR =========
            JSONObject id = new JSONObject();
            id.put("legal_name", receptorDetails.get("legal_name"));
            id.put("tax_number", receptorDetails.get("tax_number"));
            id.put("registered", Boolean.parseBoolean(receptorDetails.get("registered")));// BOOLEAN

            JSONObject recipient = new JSONObject();
            recipient.put("id", id);
            recipient.put("address_line", receptorDetails.get("address_line"));
            recipient.put("postal_code", receptorDetails.get("postal_code"));

            JSONArray recipients = new JSONArray();
            recipients.put(recipient);

            // ======== CONTENIDO DE DATA =========
            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED");
            data.put("number", invoice_number);
            data.put("text", "Factura COMPLETA");
            data.put("items", items);
            data.put("full_amount", fullAmountTotal);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "COMPLETE");
            content.put("recipients", recipients);
            content.put("data", data);

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            // =========== PETICIÓN HTTP =================            
            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            JSONObject json = new JSONObject(responseBody).getJSONObject("content");
            JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
            String qrBase64 = qr.getString("data");

            generateCompleteInvoicePDF(receptorDetails, invoice_number, itemsList, fullAmountTotal, qrBase64);

        } catch (Exception e) {
            System.err.println("Error al crear la factura completa");
            e.printStackTrace();
        }
    }

    public static void createItem(List<Map<String, String>> itemsList, String text, String quantity, String discount, String unit_amount, String iva_rate) {
        // Validar que el IVA está entre los permitidos
        List<String> validIvaRates = Arrays.asList(
                Config.IVA_GENERAL,
                Config.IVA_REDUCIDO,
                Config.IVA_SUPERREDUCIDO,
                Config.IVA_EXENTO
        );

        if (!validIvaRates.contains(iva_rate)) {
            throw new IllegalArgumentException("IVA no válido: " + iva_rate + ". Debe ser uno de: " + validIvaRates);
        }

        Map<String, String> item = new HashMap<>();
        // Añadir nota fiscal si el IVA es exento
        if (iva_rate.equals("0")) {
            item.put("text", text + " (Exento según art. 20 Ley 37/1992 del IVA)");
        } else {
            item.put("text", text);
        }
        item.put("quantity", quantity);
        item.put("discount", discount);
        item.put("unit_amount", unit_amount);
        item.put("iva_rate", iva_rate);

        itemsList.add(item);
    }

    public static Map<String, String> createReceptor(String legal_name, String tax_number, boolean registered, String address_line, String postal_code) {
        Map<String, String> receptorDetails = new HashMap<>();
        receptorDetails.put("legal_name", legal_name);
        receptorDetails.put("tax_number", tax_number);
        receptorDetails.put("registered", String.valueOf(registered));//BOOLEANO
        receptorDetails.put("address_line", address_line);
        receptorDetails.put("postal_code", postal_code);
        return receptorDetails;
    }

    public static void generateCompleteInvoicePDF(Map<String, String> receptorDetails, String number, List<Map<String, String>> itemsData, String fullAmount, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura_Completa_" + number + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA COMPLETA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Número: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(Chunk.NEWLINE);

        // ======== DATOS DEL CLIENTE ========
        Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        document.add(clientHeader);
        document.add(new Paragraph(receptorDetails.get("legal_name")));
        document.add(new Paragraph(receptorDetails.get("tax_number")));
        document.add(new Paragraph(receptorDetails.get("address_line")));
        document.add(Chunk.NEWLINE);

        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<Map<String, String>>> groupedByIVA = new HashMap<>();
        for (Map<String, String> item : itemsData) {
            String ivaRaw = item.getOrDefault("iva_rate", "21.0").trim();
            String ivaRate = String.format(Locale.US, "%.1f", Double.parseDouble(ivaRaw));
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21.0", "10.0", "4.0", "0.0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<Map<String, String>> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);

            Stream.of("Descripción", "Precio unitario", "Descuento", "Unidades", "Base imponible")
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
                String discount = item.get("discount");

                double unit = Double.parseDouble(unitAmount);
                double qty = Double.parseDouble(quantity);
                double dct = Double.parseDouble(discount);
                double base = (unit * qty) - dct;
                double iva = Double.parseDouble(ivaRate);
                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(text);
                table.addCell(String.format(Locale.US, "%.2f €", unit));
                table.addCell(String.format(Locale.US, "%.2f €", dct));
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
        Paragraph total = new Paragraph("Importe total (IVA incluido): " + String.format(Locale.US, "%.2f €", totalConIVA),
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
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));
        document.add(new Paragraph("Gracias por su confianza.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

        document.close();
        System.out.println("PDF de factura completa guardado en: " + desktopPath + fileName);
    }

}

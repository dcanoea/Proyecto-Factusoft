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
import java.util.HashMap;

public class Simplified {

    public static void create_Simplified_Invoice() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.get_First_Client_Id();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = "S-2025-017";
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== ÍTEMS DE EJEMPLO =========
            List<Map<String, String>> itemsData = new ArrayList<>();

            Map<String, String> item1 = new HashMap<>();
            item1.put("text", "Curso C+E");
            item1.put("quantity", "1.00");
            item1.put("unit_amount", "527.74");
            item1.put("iva_rate", "21.0");
            itemsData.add(item1);

            Map<String, String> item2 = new HashMap<>();
            item2.put("text", "Manual C+E");
            item2.put("quantity", "1.00");
            item2.put("unit_amount", "35.00");
            item2.put("iva_rate", "10.0");
            itemsData.add(item2);

            JSONArray items = new JSONArray();
            double totalAmount = 0.0;

            for (Map<String, String> itemData : itemsData) {
                String text = itemData.get("text");
                String quantity = itemData.get("quantity");
                String unitAmount = itemData.get("unit_amount");
                String ivaRate = itemData.getOrDefault("iva_rate", "21.0");

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

                generateSimplifiedInvoicePDF(invoice_number, itemsData, fullAmountTotal, qrBase64);
            } else {
                System.err.println("Error al crear la factura simplificada (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.out.println("Error al crear la factura");
            e.printStackTrace();
        }
    }

    public static void generateSimplifiedInvoicePDF(String number, List<Map<String, String>> itemsData, String fullAmount, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura_" + number + "_" + date + ".pdf";

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

        // ======== TABLA DE ÍTEMS ========
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        Stream.of("Descripción", "Cantidad", "Precio sin IVA", "IVA (%)", "Total con IVA")
                .forEach(headerTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    table.addCell(header);
                });

        for (Map<String, String> item : itemsData) {
            String text = item.get("text");
            String quantity = item.get("quantity");
            String unitAmount = item.get("unit_amount");
            String ivaRate = item.getOrDefault("iva_rate", "21.0");

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
            String fullAmountItem = String.format(Locale.US, "%.2f", full);

            table.addCell(text);
            table.addCell(quantity);
            table.addCell(unitAmount + " €");
            table.addCell(ivaRate);
            table.addCell(fullAmountItem + " €");
        }

        document.add(table);

        // ======== TOTALES ========
        Paragraph total = new Paragraph("Importe total (IVA incluido): " + fullAmount + " €",
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
        document.add(new Paragraph("Gracias por su confianza.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

        document.close();
        System.out.println("PDF guardado en: " + desktopPath + fileName);
    }

}

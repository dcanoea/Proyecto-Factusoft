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
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Simplified {

    public static void create_Simplified_Invoice() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.get_First_Client_Id();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = "F-2025-008";
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // Construir sistema fiscal
            JSONObject category = new JSONObject();
            category.put("type", "VAT");
            category.put("rate", "21.0");

            JSONObject system = new JSONObject();
            system.put("type", "REGULAR");
            system.put("category", category);

            // Ítem de factura
            JSONObject item = new JSONObject();
            item.put("text", "Curso ADR");
            item.put("quantity", "1.00");
            item.put("unit_amount", "210.74");
            item.put("full_amount", "255.00");
            item.put("system", system);

            JSONArray items = new JSONArray();
            items.put(item);

            // Contenido de factura
            JSONObject content = new JSONObject();
            content.put("type", "SIMPLIFIED");
            content.put("number", invoice_number);
            content.put("text", "Factura por formación ADR");
            content.put("full_amount", "255.00");
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
            System.out.println(jsonPrint.toString(2)); // Indentación de 2 espacios

            JSONObject json = new JSONObject(responseBody).getJSONObject("content");
            JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
            String qrBase64 = qr.getString("data");

            generateSimplifiedInvoicePDF(invoice_number, "Factura por formación ADR", "Curso ADR", "1.00", "210.74", "255.00", "REGULAR", qrBase64);

        } catch (Exception e) {
            System.out.println("Error al crear la factura");
            e.printStackTrace();
        }
    }

    public static void generateSimplifiedInvoicePDF(String number, String description, String itemText, String quantity, String unitAmount, String fullAmount, String systemType, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura_" + number + "_" + date + ".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        document.add(new Paragraph("Factura Simplificada", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        document.add(new Paragraph("Número: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(new Paragraph("Descripción: " + description));
        document.add(new Paragraph("Importe total: " + fullAmount + " €"));
        document.add(new Paragraph("Estado: ISSUED"));
        document.add(new Paragraph("\nÍtems:"));

        PdfPTable table = new PdfPTable(5);
        table.addCell("Descripción");
        table.addCell("Cantidad");
        table.addCell("Precio sin IVA");
        table.addCell("IVA (%)");
        table.addCell("Total con IVA");

        table.addCell(itemText);
        table.addCell(quantity);
        table.addCell(unitAmount + " €");
        table.addCell("21.0");
        table.addCell(fullAmount + " €");

        document.add(table);

        // Insertar QR
        byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.scaleToFit(100, 100);
        qrImage.setAlignment(Image.ALIGN_RIGHT);
        document.add(new Paragraph("\nQR Tributario:"));
        document.add(qrImage);

        document.close();
        System.out.println("PDF guardado en: " + desktopPath + fileName);
    }

}

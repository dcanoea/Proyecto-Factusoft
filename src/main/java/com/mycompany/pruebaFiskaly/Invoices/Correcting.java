/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

/**
 *
 * @author user
 */
public class Correcting {

    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA SIMPLIFICADA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoice_Substitution_Simplified(String original_invoice_number, String original_invoice_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.get_First_Client_Id();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = original_invoice_number + "R"; // Máximo 20 caracteres
            String invoice_series = "R-2025"; // Obligatorio en facturas rectificativas
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DEL ÍTEM =========
            String text = "Curso ADR";
            String quantity = "2.00";
            String unitAmount = "210.74";
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
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

            JSONArray items = new JSONArray();
            items.put(item);

            // ======== OBJETO INVOICE SIMPLIFICADO =========
            JSONObject invoice = new JSONObject();
            invoice.put("type", "SIMPLIFIED");
            invoice.put("number", invoice_number);
            invoice.put("series", invoice_series);
            invoice.put("text", "Factura RECTIFICATIVA");
            invoice.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            invoice.put("items", items);
            invoice.put("full_amount", fullAmount);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "SUBSTITUTION");
            content.put("code", "CORRECTION_1");
            content.put("id", original_invoice_id); // UUIDv4 válido de la factura original
            content.put("invoice", invoice);

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            // ======== PETICIÓN HTTP =========
            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);
            JSONObject jsonPrint = new JSONObject(responseBody);
            System.out.println(jsonPrint.toString(2));

            JSONObject json = new JSONObject(responseBody).getJSONObject("content");
            JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
            String qrBase64 = qr.getString("data");

            generateCorrectingInvoicePDF(invoice_number, "Factura RECTIFICATIVA", "CLIENTE SIMPLIFICADO",
                    "N/A", "N/A", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }

    //FACTURA RECTIFICATIVA DE DIFERENCIA DE FACTURA SIMPLIFICADA
    public static void createCorrectingInvoice_Differences_Simplified(String original_invoice_number, String original_invoice_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.get_First_Client_Id();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = original_invoice_number + "R";
            String invoice_series = "R-2025"; // Serie para rectificativas
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            String text = "Ajuste por diferencia de precio";
            String quantity = "1.00";
            String unitAmount = "10.00"; // solo el ajuste, no el total original
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
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

            JSONArray items = new JSONArray();
            items.put(item);

            JSONObject invoice = new JSONObject();
            invoice.put("type", "SIMPLIFIED");
            invoice.put("number", invoice_number);
            invoice.put("series", invoice_series);
            invoice.put("text", "Rectificación por diferencia");
            invoice.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            invoice.put("items", items);
            invoice.put("full_amount", fullAmount);

            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "DIFFERENCES");
            content.put("code", "CORRECTION_1");
            content.put("id", original_invoice_id); // UUID de la factura original
            content.put("invoice", invoice);

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            // ======== PETICIÓN HTTP =========
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

                generateCorrectingInvoicePDF(invoice_number, "Factura RECTIFICATIVA (DIFERENCIA)", "CLIENTE SIMPLIFICADO",
                        "N/A", "N/A", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
            } else {
                System.err.println("Error al crear la factura rectificativa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }

    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA COMPLETA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoice_Substitution_Complete(String original_invoice_number, String original_invoice_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.get_First_Client_Id();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = original_invoice_number + "R"; // Máximo 20 caracteres
            String invoice_series = "R-2025";// Obligatorio en facturas rectificativas
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DEL ÍTEM =========
            String text = "Curso ADR";
            String quantity = "2.00";
            String unitAmount = "210.74";
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
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

            JSONArray items = new JSONArray();
            items.put(item);

            // ======== RECEPTOR =========
            JSONObject id = new JSONObject();
            id.put("legal_name", "ARAGON FORMACION ACF S.L.");
            id.put("tax_number", "B22260863");
            id.put("registered", true);

            JSONObject recipient = new JSONObject();
            recipient.put("id", id);
            recipient.put("address_line", "Calle Mayor 123, Huesca");
            recipient.put("postal_code", "22001");

            JSONArray recipients = new JSONArray();
            recipients.put(recipient);

            // ======== SUBOBJETO DATA =========
            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED"); // Este campo debe ser SIMPLIFIED incluso en factura COMPLETE
            data.put("number", invoice_number);
            data.put("series", invoice_series);
            data.put("text", "Factura RECTIFICATIVA");
            data.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            data.put("items", items);
            data.put("full_amount", fullAmount);

            // ======== OBJETO INVOICE =========
            JSONObject invoice = new JSONObject();
            invoice.put("type", "COMPLETE");
            invoice.put("data", data);
            invoice.put("recipients", recipients);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "SUBSTITUTION");
            content.put("id", original_invoice_id); // UUIDv4 válido de la factura original. Se obtiene mediante método Invoices_Management.get_Invoice_Id(numeroFactura);
            content.put("invoice", invoice);
            content.put("code", "CORRECTION_1");

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            // ======== PETICIÓN HTTP =========
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

                generateCorrectingInvoicePDF(invoice_number, "Factura RECTIFICATIVA", "ARAGON FORMACION ACF S.L.",
                        "B22260863", "Calle Mayor 123, Huesca", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
            } else {
                System.err.println("Error al crear la factura rectificativa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }
    
    //MÉTODO PARA CREAR FACTURAS RECTIFICATIVAS POR DIFERENCIA PARA FACTURAS COMPLETAS
    public static void createCorrectingInvoice_Differences_Complete(String original_invoice_number, String original_invoice_id) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
        String client_id = Clients.get_First_Client_Id();
        UUID invoice_id = UUID.randomUUID();
        String invoice_number = original_invoice_number + "R";
        if (invoice_number.length() > 20) {
            invoice_number = invoice_number.substring(0, 20);
        }
        String invoice_series = "R-2025";
        String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
        String token = Authentication.retrieve_token();

        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", "application/json");
        put.setHeader("Authorization", "Bearer " + token);

        // ======== DATOS DEL ÍTEM =========
        String text = "Ajuste por diferencia de precio";
        String quantity = "1.00";
        String unitAmount = "10.00";
        String ivaRate = "21.0";

        double unit = Double.parseDouble(unitAmount);
        double qty = Double.parseDouble(quantity);
        double iva = Double.parseDouble(ivaRate);
        double full = unit * qty * (1 + iva / 100);
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

        JSONArray items = new JSONArray();
        items.put(item);

        // ======== RECEPTOR =========
        JSONObject id = new JSONObject();
        id.put("legal_name", "ARAGON FORMACION ACF S.L.");
        id.put("tax_number", "B22260863");
        id.put("registered", true);

        JSONObject recipient = new JSONObject();
        recipient.put("id", id);
        recipient.put("address_line", "Calle Mayor 123, Huesca");
        recipient.put("postal_code", "22001");

        JSONArray recipients = new JSONArray();
        recipients.put(recipient);

        // ======== SUBOBJETO DATA =========
        JSONObject data = new JSONObject();
        data.put("type", "SIMPLIFIED");
        data.put("number", invoice_number);
        data.put("series", invoice_series);
        data.put("text", "Rectificación por diferencia");
        data.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        data.put("items", items);
        data.put("full_amount", fullAmount);

        // ======== OBJETO INVOICE =========
        JSONObject invoice = new JSONObject();
        invoice.put("type", "COMPLETE");
        invoice.put("data", data);
        invoice.put("recipients", recipients);

        // ======== CONTENIDO PRINCIPAL =========
        JSONObject content = new JSONObject();
        content.put("type", "CORRECTING");
        content.put("method", "DIFFERENCES");
        content.put("code", "CORRECTION_1");
        content.put("id", original_invoice_id);
        content.put("invoice", invoice);

        JSONObject body = new JSONObject();
        body.put("content", content);

        put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

        // ======== PETICIÓN HTTP =========
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

            generateCorrectingInvoicePDF(invoice_number, "DIFFERENCES", "ARAGON FORMACION ACF S.L.",
                    "B22260863", "Calle Mayor 123, Huesca", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
        } else {
            System.err.println("Error al crear la factura rectificativa (" + statusCode + ")");
        }

    } catch (Exception e) {
        System.err.println("Error al crear la factura rectificativa");
        e.printStackTrace();
    }
}


    public static void generateCorrectingInvoicePDF(
            String number, String correctionType, String clientName, String clientNIF, String clientAddress,
            String itemText, String quantity, String unitAmount, String ivaRate, String fullAmount, String qrBase64) {

        try {
            String desktopPath = System.getProperty("user.home") + "/Desktop/";
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String fileName = "Factura_Rectificativa_" + number + "_" + date + ".pdf";

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
            document.open();

            // ======== CABECERA ========
            String titleText = "FACTURA RECTIFICATIVA";
            if ("DIFFERENCE".equalsIgnoreCase(correctionType)) {
                titleText += " (POR DIFERENCIA)";
            } else if ("SUBSTITUTION".equalsIgnoreCase(correctionType)) {
                titleText += " (POR SUSTITUCIÓN)";
            }

            Paragraph title = new Paragraph(titleText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Número: " + number));
            document.add(new Paragraph("Fecha de emisión: " + date));
            document.add(new Paragraph("Tipo de corrección: " + correctionType));
            document.add(Chunk.NEWLINE);

            // ======== DATOS DEL CLIENTE ========
            Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(clientHeader);
            document.add(new Paragraph("Nombre o Razón Social: " + clientName));
            document.add(new Paragraph("NIF: " + clientNIF));
            document.add(new Paragraph("Dirección: " + clientAddress));
            document.add(Chunk.NEWLINE);

            // ======== TABLA DE ÍTEMS ========
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Stream.of("Descripción", "Cantidad", "Precio Unitario", "IVA (%)", "Total con IVA")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                        table.addCell(header);
                    });

            table.addCell(itemText);
            table.addCell(quantity);
            table.addCell(unitAmount + " €");
            table.addCell(ivaRate);
            table.addCell(fullAmount + " €");

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
            System.out.println("PDF de factura rectificativa guardado en: " + desktopPath + fileName);
        } catch (Exception ex) {
            System.out.print("Error al crear factura ");
            ex.printStackTrace();
        }
    }
}

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

public class Correcting {

    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA COMPLETA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoiceSubstitutionComplete(int original_invoice_number_int, int new_invoice_number, List<Map<String, String>> itemsList, Map<String, String> receptorDetails) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String original_invoice_number = String.valueOf(original_invoice_number_int);
            String original_invoice_id = InvoicesManagement.getInvoiceIDByNumber(original_invoice_number);

            String invoice_number = String.valueOf(new_invoice_number);
            String invoice_series = "2025";// Obligatorio en facturas rectificativas
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DE LÍNEAS DE LA FACTURA =========
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

            // ======== SUBOBJETO DATA =========
            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED"); // Este campo debe ser SIMPLIFIED incluso en factura COMPLETE
            data.put("number", invoice_number);
            data.put("series", invoice_series);
            data.put("text", "Factura RECTIFICATIVA");
            //data.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            data.put("items", items);
            data.put("full_amount", fullAmountTotal);

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
            JSONObject json = new JSONObject(responseBody).getJSONObject("content");
            JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
            String qrBase64 = qr.getString("data");

            generateCorrectingInvoicePDF(original_invoice_number, receptorDetails, invoice_number, itemsList, fullAmountTotal, qrBase64);

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }

    public static void createItem(List<Map<String, String>> itemsList, String text, String quantity, String unit_amount, String iva_rate) {
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

    public static void generateCorrectingInvoicePDF(String original_invoice_number, Map<String, String> receptorDetails, String number, List<Map<String, String>> itemsData, String fullAmount, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura_Rectificativa_" + number + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA RECTIFICATIVA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
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
        document.add(new Paragraph("Sustituye a " + original_invoice_number));
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
        System.out.println("PDF de factura rectificativa guardado en: " + desktopPath + fileName);
    }

    /*
        //MÉTODO PARA CREAR FACTURAS RECTIFICATIVAS POR DIFERENCIA PARA FACTURAS COMPLETAS
    public static void createCorrectingInvoiceDifferencesComplete(String original_invoice_number, String original_invoice_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = original_invoice_number + "R";
            if (invoice_number.length() > 20) {
                invoice_number = invoice_number.substring(0, 20);
            }
            String invoice_series = "R-2025";
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieveToken();

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
     */
 /*
    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA SIMPLIFICADA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoiceSubstitutionSimplified(String original_invoice_number, String original_invoice_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = original_invoice_number + "R"; // Máximo 20 caracteres
            String invoice_series = "R-2025"; // Obligatorio en facturas rectificativas
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieveToken();

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
    }*/
 /*
    //FACTURA RECTIFICATIVA DE DIFERENCIA DE FACTURA SIMPLIFICADA
    public static void createCorrectingInvoiceDifferencesSimplified(String original_invoice_number, String original_invoice_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = original_invoice_number + "R";
            String invoice_series = "R-2025"; // Serie para rectificativas
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieveToken();

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
    }*/
}

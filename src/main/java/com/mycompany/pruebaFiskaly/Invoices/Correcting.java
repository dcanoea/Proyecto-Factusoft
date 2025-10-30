package com.mycompany.pruebaFiskaly.Invoices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class Correcting {

    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA COMPLETA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoiceSubstitutionComplete(String originalInvoiceNumber, String invoiceNumber, List<JSONObject> itemsList, List<JSONObject> suppliedItems, List<JSONObject> globalDiscounts, Map<String, String> receptorDetails) {

        // Cliente HTTP reutilizable para enviar la petición PUT de la factura.
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            double totalAmount = 0.0; //Variable para contabilizar el monto total de la factura

            // ======== ITEMS (LINEAS DE FACTURA) =========
            JSONArray items = new JSONArray();
            for (JSONObject itemData : itemsList) {
                double itemTotalAmount = InvoiceHelpers.getItem(itemData, items);
                totalAmount += itemTotalAmount;
            }

            // ======== ITEMS SUPLIDOS =========
            totalAmount = InvoiceHelpers.getSuppliedItems(suppliedItems, items, totalAmount);

            // ======== ITEMS DESCUENTOS GLOBALES =========
            totalAmount = InvoiceHelpers.getGlobalDiscounts(globalDiscounts, items, totalAmount);

            // REDONDEO MONTO TOTAL PARA EVITAR ERRORES DE VALIDACIÓN
            String fullAmountTotal = String.format(Locale.US, "%.2f", totalAmount);

            // ======== CONSTRUCCIÓN JSON =========
            JSONArray recipients = InvoiceHelpers.getRecipient(receptorDetails);
            JSONObject data = InvoiceHelpers.getDataCorrectingInvoice(invoiceNumber, items, fullAmountTotal);
            JSONObject invoice = InvoiceHelpers.getInvoice(data, recipients);
            JSONObject content = InvoiceHelpers.getContentCorrectingSubstitution(originalInvoiceNumber, invoice);
            JSONObject body = InvoiceHelpers.getBody(content);

            // ========= PETICIÓN API=========
            HttpPut put = InvoiceHelpers.putRequest(body);

            // ========= RESPUESTA DE LA API=========
            String responseBody = InvoiceHelpers.requestAPI(client, put);

            // ========= RECUPERAR QR DE LA RESPUESTA =========
            String qrBase64 = InvoiceHelpers.getQR(responseBody);

            // ========= GENERAR PDF DE FACTURA =========
            PdfTools.generateCorrectingInvoicePDF(originalInvoiceNumber, receptorDetails, invoiceNumber, itemsList, suppliedItems, globalDiscounts, fullAmountTotal, qrBase64);

        } catch (Exception e) {
            System.err.println("Error al crear la factura completa");
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException ioe) {
                System.err.println("Error al cerrar el cliente HTTP");
                ioe.printStackTrace();
            }
        }
    }
}
/*
    //MÉTODO PARA CREAR FACTURAS RECTIFICATIVAS POR DIFERENCIA PARA FACTURAS COMPLETAS
    public static void createCorrectingInvoiceDifferencesComplete(String originalInvoiceNumber, String originalInvoiceID) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
    String clientID = Clients.getFirstClientID();
    UUID invoiceID = UUID.randomUUID();
    String invoiceNumber = originalInvoiceNumber + "R";
    if (invoiceNumber.length() > 20) {
    invoiceNumber = invoiceNumber.substring(0, 20);
    }
    String invoiceSeries = "R-2025";
    String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
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
    data.put("number", invoiceNumber);
    data.put("series", invoiceSeries);
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
    content.put("id", originalInvoiceID);
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
    generateCorrectingInvoicePDF(invoiceNumber, "DIFFERENCES", "ARAGON FORMACION ACF S.L.",
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
    public static void createCorrectingInvoiceSubstitutionSimplified(String originalInvoiceNumber, String originalInvoiceID) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
    String clientID = Clients.getFirstClientID();
    UUID invoiceID = UUID.randomUUID();
    String invoiceNumber = originalInvoiceNumber + "R"; // Máximo 20 caracteres
    String invoiceSeries = "R-2025"; // Obligatorio en facturas rectificativas
    String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
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
    invoice.put("number", invoiceNumber);
    invoice.put("series", invoiceSeries);
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
    content.put("id", originalInvoiceID); // UUIDv4 válido de la factura original
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
    generateCorrectingInvoicePDF(invoiceNumber, "Factura RECTIFICATIVA", "CLIENTE SIMPLIFICADO",
    "N/A", "N/A", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
    } catch (Exception e) {
    System.err.println("Error al crear la factura rectificativa");
    e.printStackTrace();
    }
    }*/
 /*
    //FACTURA RECTIFICATIVA DE DIFERENCIA DE FACTURA SIMPLIFICADA
    public static void createCorrectingInvoiceDifferencesSimplified(String originalInvoiceNumber, String originalInvoiceID) {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
    String clientID = Clients.getFirstClientID();
    UUID invoiceID = UUID.randomUUID();
    String invoiceNumber = originalInvoiceNumber + "R";
    String invoiceSeries = "R-2025"; // Serie para rectificativas
    String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
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
    invoice.put("number", invoiceNumber);
    invoice.put("series", invoiceSeries);
    invoice.put("text", "Rectificación por diferencia");
    invoice.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    invoice.put("items", items);
    invoice.put("full_amount", fullAmount);
    JSONObject content = new JSONObject();
    content.put("type", "CORRECTING");
    content.put("method", "DIFFERENCES");
    content.put("code", "CORRECTION_1");
    content.put("id", originalInvoiceID); // UUID de la factura original
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
    generateCorrectingInvoicePDF(invoiceNumber, "Factura RECTIFICATIVA (DIFERENCIA)", "CLIENTE SIMPLIFICADO",
    "N/A", "N/A", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
    } else {
    System.err.println("Error al crear la factura rectificativa (" + statusCode + ")");
    }
    } catch (Exception e) {
    System.err.println("Error al crear la factura rectificativa");
    e.printStackTrace();
    }
    }*/

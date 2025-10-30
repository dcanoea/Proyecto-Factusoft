/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices;

import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Clients;
import com.mycompany.pruebaFiskaly.Config;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class InvoiceHelpers {

    public static String invoiceSeries = "R2025";// Obligatorio en facturas rectificativas

    public static JSONObject getCategory(String ivaRate) throws JSONException {
        // Tipo IVA
        JSONObject category = new JSONObject();
        if (ivaRate.equalsIgnoreCase("exento")) {
            category.put("type", "NO_VAT");
            category.put("cause", "TAXABLE_EXEMPT_1");
        } else {
            category.put("type", "VAT");
            category.put("rate", ivaRate);
        }
        return category;
    }

    public static JSONObject getSystem(JSONObject category) throws JSONException {
        // Régimen Fiscal
        JSONObject system = new JSONObject();
        system.put("type", "REGULAR");
        system.put("category", category);
        return system;
    }

    public static double getItem(JSONObject itemData, JSONArray items) throws JSONException {
        String text = itemData.optString("text", "");
        String quantity = itemData.optString("quantity", "0").trim();
        String unitAmount = itemData.optString("unit_amount", "0").trim();
        String discount = itemData.optString("discount", "0").trim();
        String ivaRate = itemData.optString("iva_rate", "21.0").trim();

        double unit = 0.0;
        if (!unitAmount.isEmpty()) {
            unit = Double.parseDouble(unitAmount);
        }

        double qty = 0.0;
        if (!quantity.isEmpty()) {
            qty = Double.parseDouble(quantity);
        }

        double dct = 0.0;
        if (!discount.isEmpty()) {
            dct = Double.parseDouble(discount);
        }

        double base = (unit - dct) * qty;

        double iva = 0.0;
        double totalAmount = 0.0;
        if (ivaRate.equalsIgnoreCase("exento")) {
            iva = 0.0;
            totalAmount = base;
        } else {
            iva = Double.parseDouble(ivaRate);
            totalAmount = base * (1 + iva / 100);
        }

        String fullAmount = String.format(Locale.US, "%.2f", totalAmount);

        JSONObject category = InvoiceHelpers.getCategory(ivaRate);
        JSONObject system = getSystem(category);

        JSONObject item = new JSONObject();
        item.put("text", text);
        item.put("quantity", quantity);
        item.put("unit_amount", unitAmount);
        item.put("discount", discount);
        item.put("full_amount", fullAmount);
        item.put("system", system);

        items.put(item);
        return totalAmount;
    }

    public static double getSuppliedItems(List<JSONObject> suppliedItems, JSONArray items, double totalAmount) throws JSONException {
        // Ítems suplidos
        for (JSONObject suplido : suppliedItems) {
            items.put(suplido);
            String suplidoFullStr = "0";
            if (suplido.has("full_amount")) {
                Object fa = suplido.get("full_amount");
                if (fa != null) {
                    suplidoFullStr = String.valueOf(fa);
                }
            }
            suplidoFullStr = suplidoFullStr.trim().replace(",", ".");
            double suplidoFull = 0.0;
            try {
                suplidoFull = Double.parseDouble(suplidoFullStr);
            } catch (NumberFormatException e) {
                System.err.println("full_amount inv\u00e1lido en suplido: '" + suplidoFullStr + "'. Asumiendo 0.00");
            }
            totalAmount += suplidoFull;
        }
        return totalAmount;
    }

    public static double getGlobalDiscounts(List<JSONObject> globalDiscounts, JSONArray items, double totalAmount) throws JSONException {
        // Descuentos globales
        for (JSONObject discount : globalDiscounts) {
            items.put(discount);
            String discountFullStr = "0";
            if (discount.has("full_amount")) {
                Object fa = discount.get("full_amount");
                if (fa != null) {
                    discountFullStr = String.valueOf(fa);
                }
            }
            discountFullStr = discountFullStr.trim().replace(",", ".");
            double discountFull = 0.0;
            try {
                discountFull = Double.parseDouble(discountFullStr);
            } catch (NumberFormatException e) {
                System.err.println("full_amount inv\u00e1lido en descuento: '" + discountFullStr + "'. Asumiendo 0.00");
            }
            totalAmount += discountFull;
        }
        return totalAmount;
    }

    public static JSONArray getRecipient(Map<String, String> receptorDetails) throws JSONException {
        // ========= RECEPTOR =========
        JSONObject id = new JSONObject();
        id.put("legal_name", receptorDetails.get("legal_name"));
        id.put("tax_number", receptorDetails.get("tax_number"));
        id.put("registered", Boolean.parseBoolean(receptorDetails.get("registered")));
        JSONObject recipient = new JSONObject();
        recipient.put("id", id);
        recipient.put("address_line", receptorDetails.get("address_line"));
        recipient.put("postal_code", receptorDetails.get("postal_code"));
        JSONArray recipients = new JSONArray();
        recipients.put(recipient);
        return recipients;
    }

    public static JSONObject getDataCompleteInvoice(String invoiceNumber, JSONArray items, String fullAmountTotal) throws JSONException {
        // ========= DATA =========
        JSONObject data = new JSONObject();
        data.put("type", "SIMPLIFIED");
        data.put("number", invoiceNumber);
        data.put("text", "Factura COMPLETA");
        data.put("items", items);
        data.put("full_amount", fullAmountTotal);
        return data;
    }

    public static JSONObject getDataCorrectingInvoice(String invoiceNumber, JSONArray items, String fullAmountTotal) throws JSONException {
        // ======== SUBOBJETO DATA =========
        JSONObject data = new JSONObject();
        data.put("type", "SIMPLIFIED"); // Este campo debe ser SIMPLIFIED incluso en factura COMPLETE
        data.put("number", invoiceNumber);
        data.put("series", invoiceSeries);
        data.put("text", "Factura RECTIFICATIVA");
        data.put("items", items);
        data.put("full_amount", fullAmountTotal);
        return data;
    }

    public static JSONObject getContent(JSONArray recipients, JSONObject data) throws JSONException {
        // ========= CONTENT =========
        JSONObject content = new JSONObject();
        content.put("type", "COMPLETE");
        content.put("recipients", recipients);
        content.put("data", data);
        return content;
    }

    public static String getQR(String responseBody) throws JSONException {
        JSONObject json = new JSONObject(responseBody).getJSONObject("content");
        JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
        String qrBase64 = qr.getString("data");
        return qrBase64;
    }

    public static JSONObject createItem(String text, String quantity, String discount, String unitAmount, String ivaRate) {
        List<String> validIvaRates = Arrays.asList(Config.IVA_GENERAL, Config.IVA_REDUCIDO, Config.IVA_SUPERREDUCIDO, Config.IVA_EXENTO);
        if (!validIvaRates.contains(ivaRate)) {
            throw new IllegalArgumentException("IVA no v\u00e1lido: " + ivaRate + ". Debe ser uno de: " + validIvaRates);
        }
        JSONObject item = new JSONObject();
        if (ivaRate.equals(Config.IVA_EXENTO)) {
            item.put("text", text + " (Exento seg\u00fan art. 20 Ley 37/1992 del IVA)");
            item.put("iva_rate", "exento");
        } else {
            item.put("text", text);
            item.put("iva_rate", ivaRate);
        }
        item.put("quantity", quantity);
        item.put("discount", discount);
        item.put("unit_amount", unitAmount);

        return item;
    }

    public static JSONObject createSupplied(String text, String quantity, String unitAmount, String fullAmount) {
        JSONObject category = new JSONObject();
        category.put("type", "NO_VAT");
        category.put("cause", "NON_TAXABLE_4");
        JSONObject system = new JSONObject();
        system.put("category", category);
        system.put("type", "REGULAR");
        JSONObject item = new JSONObject();
        item.put("text", text);
        item.put("quantity", quantity);
        item.put("unit_amount", unitAmount);
        item.put("full_amount", fullAmount);
        item.put("system", system);
        //item.put("vat_type", "IVA");
        return item;
    }

    public static JSONObject createGlobalDiscount(String iva, String quantity, String unitAmount) {
        double unit = Double.parseDouble(unitAmount);
        double qty = Double.parseDouble(quantity);
        double IVA;
        if (iva.equals(Config.IVA_EXENTO)) {
            IVA = 0.0;
        } else {
            IVA = Double.parseDouble(iva);
        }
        double fullAmount = (unit * qty) * (1 + (IVA / 100));
        String fullAmountTotal = String.format(Locale.US, "%.2f", fullAmount);
        JSONObject category = new JSONObject();
        if (iva.equals(Config.IVA_EXENTO)) {
            category.put("type", "NO_VAT");
            category.put("cause", "TAXABLE_EXEMPT_1");
        } else {
            category.put("type", "VAT");
            category.put("rate", iva);
        }
        JSONObject system = getSystem(category);
        JSONObject item = new JSONObject();
        item.put("text", "Descuento");
        item.put("quantity", quantity);
        item.put("unit_amount", unitAmount);
        item.put("full_amount", fullAmountTotal);
        item.put("system", system);
        return item;
    }

    public static Map<String, String> createReceptor(String legalName, String taxNumber, boolean registered, String addressLine, String postalCode) {
        Map<String, String> receptorDetails = new HashMap<>();
        receptorDetails.put("legal_name", legalName);
        receptorDetails.put("tax_number", taxNumber);
        receptorDetails.put("registered", String.valueOf(registered)); //BOOLEANO
        receptorDetails.put("address_line", addressLine);
        receptorDetails.put("postal_code", postalCode);
        return receptorDetails;
    }

    public static HttpPut putRequest(JSONObject body) throws IOException, JSONException {
        // ========= PETICIÓN API=========
        String clientID = Clients.getFirstClientID();
        String token = Authentication.retrieveToken();
        UUID invoiceID = UUID.randomUUID();
        String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", "application/json");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
        System.out.println(body.toString(5));
        return put;
    }

    public static String requestAPI(CloseableHttpClient client, HttpPut put) throws IOException {
        // ========= RESPUESTA API=========
        HttpResponse response = client.execute(put);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println("C\u00f3digo de respuesta: " + statusCode);
        System.out.println("Respuesta completa del servidor:");
        System.out.println(responseBody);
        return responseBody;
    }

    public static JSONObject getBody(JSONObject content) throws JSONException {
        // ========= JSON BODY ==========
        JSONObject body = new JSONObject();
        body.put("content", content);
        return body;
    }

    public static JSONObject getContentCorrectingSubstitution(String invoiceNumber, JSONObject invoice) throws JSONException {
        // ======== CONTENIDO PRINCIPAL =========
        JSONObject content = new JSONObject();
        content.put("type", "CORRECTING");
        content.put("method", "SUBSTITUTION");
        content.put("id", InvoicesManagement.getInvoiceIDByNumber(invoiceNumber)); // UUIDv4 válido de la factura original.
        content.put("invoice", invoice);
        content.put("code", "CORRECTION_1");
        return content;
    }

    public static JSONObject getInvoice(JSONObject data, JSONArray recipients) throws JSONException {
        // ======== OBJETO INVOICE =========
        JSONObject invoice = new JSONObject();
        invoice.put("type", "COMPLETE");
        invoice.put("data", data);
        invoice.put("recipients", recipients);
        return invoice;
    }
}

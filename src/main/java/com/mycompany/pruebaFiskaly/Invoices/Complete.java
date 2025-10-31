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
import static com.mycompany.pruebaFiskaly.Invoices.InvoiceHelpers.setSystem;

public class Complete {

    public static void createCompleteInvoice(String invoiceNumber, List<JSONObject> itemsList, List<JSONObject> suppliedItems,
            List<JSONObject> globalDiscounts, Map<String, String> receptorDetails) {

        // Cliente HTTP reutilizable para enviar la petición PUT de la factura.
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            double totalAmount = 0.0; //Variable para contabilizar el monto total de la factura

            // ======== ITEMS (LINEAS DE FACTURA) =========
            JSONArray items = new JSONArray();
            for (JSONObject itemData : itemsList) {
                double itemTotalAmount = InvoiceHelpers.setItem(itemData, items);
                totalAmount += itemTotalAmount;
            }

            // ======== ITEMS SUPLIDOS =========
            totalAmount = InvoiceHelpers.setSuppliedItems(suppliedItems, items, totalAmount);

            // ======== ITEMS DESCUENTOS GLOBALES =========
            totalAmount = InvoiceHelpers.setGlobalDiscounts(globalDiscounts, items, totalAmount);

            // REDONDEO MONTO TOTAL PARA EVITAR ERRORES DE VALIDACIÓN
            String fullAmountTotal = String.format(Locale.US, "%.2f", totalAmount);

            // ======== CONSTRUCCIÓN JSON =========
            JSONArray recipients = InvoiceHelpers.setRecipient(receptorDetails);
            JSONObject data = InvoiceHelpers.setDataCompleteInvoice(invoiceNumber, items, fullAmountTotal);
            JSONObject content = InvoiceHelpers.setContent(recipients, data);
            JSONObject body = InvoiceHelpers.setBody(content);

            // ========= PETICIÓN API=========
            HttpPut put = InvoiceHelpers.putRequest(body);

            // ========= RESPUESTA DE LA API=========
            String responseBody = InvoiceHelpers.requestAPI(client, put);

            // ========= RECUPERAR QR DE LA RESPUESTA =========
            String qrBase64 = InvoiceHelpers.setQR(responseBody);

            // ========= GENERAR PDF DE FACTURA =========
            PdfTools.generateCompleteInvoicePDF(receptorDetails, invoiceNumber, itemsList, suppliedItems, globalDiscounts, fullAmountTotal, qrBase64);

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
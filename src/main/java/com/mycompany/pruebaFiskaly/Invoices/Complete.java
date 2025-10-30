package com.mycompany.pruebaFiskaly.Invoices;

import static com.mycompany.pruebaFiskaly.Invoices.InvoiceHelpers.getSystem;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

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
            JSONObject data = InvoiceHelpers.getDataCompleteInvoice(invoiceNumber, items, fullAmountTotal);
            JSONObject content = InvoiceHelpers.getContent(recipients, data);
            JSONObject body = InvoiceHelpers.getBody(content);

            // ========= PETICIÓN API=========
            HttpPut put = InvoiceHelpers.putRequest(body);

            // ========= RESPUESTA DE LA API=========
            String responseBody = InvoiceHelpers.requestAPI(client, put);

            // ========= RECUPERAR QR DE LA RESPUESTA =========
            String qrBase64 = InvoiceHelpers.getQR(responseBody);

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
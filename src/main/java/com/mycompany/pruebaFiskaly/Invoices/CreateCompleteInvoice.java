package com.mycompany.pruebaFiskaly.Invoices;

import com.mycompany.pruebaFiskaly.Config;
import com.mycompany.pruebaFiskaly.Invoices.DTO.CompleteDTO;
import com.mycompany.pruebaFiskaly.ConnectionAPI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;

public class CreateCompleteInvoice {

    public static void createInvoice(CompleteDTO complete) {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            // ======== Convertimos DTO en JSON ========
            String body = JsonUtil.toJson(complete);

            // ======== Llamada a la API ========
            HttpPut put = ConnectionAPI.putRequest(Config.CREATE_INVOICE, body);
            String responseBody = ConnectionAPI.requestAPI(client, put);

            // ======== Extraemos QR de la respuesta ========
            String qrBase64 = PdfTools.setQR(responseBody);

            // ======= Generamos el PDF ========
            PdfTools.generateCompleteInvoicePDF(complete, qrBase64);

            System.out.println("Factura completa generada con éxito.");

        } catch (IOException | JSONException e) {
            Logger.getLogger(CreateCompleteInvoice.class.getName()).log(Level.SEVERE, "Error al crear la factura", e);
        } catch (Exception e) {
            Logger.getLogger(CreateCompleteInvoice.class.getName()).log(Level.SEVERE, "Error general", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                Logger.getLogger(CreateCompleteInvoice.class.getName()).log(Level.SEVERE, "Error al cerrar cliente HTTP", e);
            }
        }
    }
}

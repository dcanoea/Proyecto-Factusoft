package com.mycompany.fiskaly.Invoices;

import com.mycompany.fiskaly.Config;
import com.mycompany.fiskaly.ConnectionAPI;
import com.mycompany.fiskaly.Invoices.DTO.ContentCorrectingDTO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;

public class CreateCorrectingInvoice {

    public static void createCorrectingInvoice(ContentCorrectingDTO content) {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            // ======== Convertimos DTO en JSON ========
            String jsonContent = JsonUtil.toJson(content);

            // ======== Envolvemos en raíz "content" ========
            String body = "{ \"content\": " + jsonContent + " }";

            // ======== Llamada a la API ========
            HttpPut put = ConnectionAPI.putRequest(Config.CREATE_INVOICE, body);
            String responseBody = ConnectionAPI.requestAPI(client, put);

            // ======== Extraemos QR de la respuesta ========
            String qrBase64 = PdfTools.setQR(responseBody);

            // ======= Generamos el PDF ========
            PdfTools.generateCorrectingInvoicePDF(content, qrBase64);

            System.out.println("Factura rectificativa generada con éxito.");

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

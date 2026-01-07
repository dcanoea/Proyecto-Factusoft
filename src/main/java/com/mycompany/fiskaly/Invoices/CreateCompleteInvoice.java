package com.mycompany.fiskaly.Invoices;

import com.mycompany.fiskaly.Config;
import com.mycompany.fiskaly.ConnectionAPI;
import com.mycompany.fiskaly.Invoices.DTO.ContentCompleteDTO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;

public class CreateCompleteInvoice {

    public static int createInvoice(ContentCompleteDTO content) {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            // ======== Convertimos DTO en JSON ========
            String jsonContent = JsonUtil.toJson(content);

            // ======== Envolvemos en raíz "content" ========
            String body = "{ \"content\": " + jsonContent + " }";

            // ======== Llamada a la API ========
            HttpPut put = ConnectionAPI.putRequest(Config.CREATE_INVOICE, body);
            String responseBody = ConnectionAPI.requestAPI(client, put);

            // RECUPERAMOS EL CÓDIGO DE ESTADO HTTP
            int statusCode = ConnectionAPI.getStatusCode();

            // --- CORRECCIÓN: SOLO GENERAMOS PDF SI ES ÉXITO (200 o 201) ---
            if (statusCode >= 200 && statusCode < 300) {

                // 1. Extraemos QR de la respuesta (Solo si la respuesta es OK)
                String qrBase64 = PdfTools.setQR(responseBody);

                // 2. Generamos el PDF
                PdfTools.generateCompleteInvoicePDF(content, qrBase64);

                System.out.println("Factura completa generada con éxito.");

            } else {
                // SI FALLA, MOSTRAMOS EL ERROR DEL SERVIDOR PERO NO INTENTAMOS CREAR PDF
                System.err.println("FALLO AL ENVIAR FACTURA. Código: " + statusCode);
                System.err.println("Respuesta del servidor: " + responseBody);
            }

        } catch (IOException | JSONException e) {
            Logger.getLogger(CreateCompleteInvoice.class.getName()).log(Level.SEVERE, "Error al procesar la factura", e);
        } catch (Exception e) {
            Logger.getLogger(CreateCompleteInvoice.class.getName()).log(Level.SEVERE, "Error general", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                Logger.getLogger(CreateCompleteInvoice.class.getName()).log(Level.SEVERE, "Error al cerrar cliente HTTP", e);
            }
        }
        return ConnectionAPI.getStatusCode();
    }
}

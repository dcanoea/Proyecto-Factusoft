/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;

/**
 *
 * @author user
 */
public class CreateCorrectingInvoiceDTO {

    public static void createCorrectingInvoice(CorrectingDTO correcting) {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            // ======== Convertimos DTO en JSON ========
            String body = JsonUtil.toJson(correcting);

            // ======== Llamada a la API ========
            HttpPut put = ConnectionAPI.putRequest(body);
            String responseBody = ConnectionAPI.requestAPI(client, put);

            // ======== Extraemos QR de la respuesta ========
            String qrBase64 = PdfToolsDTO.setQR(responseBody);

            // ======= Generamos el PDF ========
            PdfToolsDTO.generateCorrectingInvoicePDF(correcting, qrBase64);

            System.out.println("Factura rectificativa generada con éxito.");

        } catch (IOException | JSONException e) {
            Logger.getLogger(CreateCompleteInvoiceDTO.class.getName()).log(Level.SEVERE, "Error al crear la factura", e);
        } catch (Exception e) {
            Logger.getLogger(CreateCompleteInvoiceDTO.class.getName()).log(Level.SEVERE, "Error general", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                Logger.getLogger(CreateCompleteInvoiceDTO.class.getName()).log(Level.SEVERE, "Error al cerrar cliente HTTP", e);
            }
        }
    }
}

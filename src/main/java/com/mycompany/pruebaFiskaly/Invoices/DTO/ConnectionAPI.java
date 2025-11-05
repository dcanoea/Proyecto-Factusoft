/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Clients;
import com.mycompany.pruebaFiskaly.Config;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class ConnectionAPI {

    public static HttpPut putRequest(String body) throws IOException, JSONException {
        // ========= PETICIÓN API=========
        String clientID = Clients.getFirstClientID();
        String token = Authentication.retrieveToken();
        UUID invoiceID = UUID.randomUUID();
        String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", "application/json");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        System.out.println(body);
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
}

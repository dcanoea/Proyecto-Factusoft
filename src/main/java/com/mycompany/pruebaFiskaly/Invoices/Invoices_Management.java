/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices;

import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Config;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class Invoices_Management {

    // Este endpoint obtiene una lista de las facturas emitidas desde un dispositivo cliente.
    public static void list_Invoices(String client_id) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices";
            String token = Authentication.retrieve_token();

            HttpGet get = new HttpGet(url);
            get.setHeader("Content-Type", "application/json");
            get.setHeader("Authorization", "Bearer " + token);

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor:");
            JSONObject json = new JSONObject(responseBody);
            System.out.println(json.toString(2)); // Indentación de 2 espacios

        } catch (Exception e) {
            System.out.println("Error al recuperar los clients");
            e.printStackTrace();
        }
    }

    // Obtiene id factura filtrando por nº de factura
    public static String get_Invoice_Id(String number) {
        String invoice_id = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + "/invoices?number=" + number;
            String token = Authentication.retrieve_token();

            HttpGet get = new HttpGet(url);
            get.setHeader("Content-Type", "application/json");
            get.setHeader("Authorization", "Bearer " + token);

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            JSONObject json = new JSONObject(responseBody);

            JSONArray results = json.getJSONArray("results");
            JSONObject content = results.getJSONObject(0).getJSONObject("content");
            invoice_id = content.getString("id");

        } catch (Exception e) {
            System.out.println("Error al recuperar la factura");
            e.printStackTrace();
        }
        System.out.println("invoice_id -> " + invoice_id);
        return invoice_id;
    }
}

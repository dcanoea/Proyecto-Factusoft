/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Clients;
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
public class InvoicesManagement {

    // Este endpoint obtiene una lista de las facturas emitidas desde un dispositivo cliente.
    public static void listInvoices() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices";
            String token = Authentication.retrieveToken();

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
    public static String getInvoiceID(String number) {
        String invoice_id = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + "/invoices?number=" + number;
            String token = Authentication.retrieveToken();

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

    // Obtiene los detalles de una factura
    public static JSONObject retrieveInvoice(String invoice_id) {
        JSONObject factura = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String token = Authentication.retrieveToken();
            String client_id = Clients.getFirstClientID();
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;

            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + token);
            get.setHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);

            JSONObject json = new JSONObject(responseBody);
            if (json.has("content")) {
                factura = json.getJSONObject("content");
            } else {
                System.err.println("No se encontró el contenido de la factura: " + invoice_id);
            }
            System.out.println(json.toString(2));

        } catch (Exception e) {
            System.err.println("Error al recuperar la factura: " + invoice_id);
            e.printStackTrace();
        }
        return factura;
    }

    // Obtiene los detalles de una factura
    public static String getFullAmount(String invoice_id) {
        String full_amount = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String token = Authentication.retrieveToken();
            String client_id = Clients.getFirstClientID();
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;

            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + token);
            get.setHeader("Content-Type", "application/json");

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);

            JSONObject json = new JSONObject(responseBody);
            if (json.has("content")) {
                json.getJSONObject("content");
            } else {
                System.err.println("No se encontró el contenido de la factura: " + invoice_id);
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = json.toString();
            JsonNode root = mapper.readTree(jsonString);

            // Accede al campo "data" que contiene un JSON como string
            String innerJsonString = root.path("content").path("data").asText();

            // Parsear ese string como JSON
            JsonNode innerJson = mapper.readTree(innerJsonString);

            // Extraer el valor de "full_amount"
            full_amount = innerJson.path("full_amount").asText();

        } catch (Exception e) {
            System.err.println("Error al recuperar la factura: " + invoice_id);
            e.printStackTrace();
        }
        System.out.println("Total Factura " + invoice_id + " -> " + full_amount);
        return full_amount;
    }
}

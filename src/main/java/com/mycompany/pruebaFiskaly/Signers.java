/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class Signers {

    public static void create_Signer() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            UUID uuid = UUID.randomUUID();
            String url = Config.BASE_URL + "/signers/" + uuid.toString();
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // Enviar solicitud
            StringEntity entity = new StringEntity("{}", StandardCharsets.UTF_8);
            put.setEntity(entity);

            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);

        } catch (Exception e) {
            System.out.println("Error al crear el signer");
            e.printStackTrace();
        }
    }

    public static String list_Signers() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + "/signers";
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

            return responseBody;

        } catch (Exception e) {
            System.out.println("Error al recuperar los signers");
            e.printStackTrace();
            return null;
        }
    }

    public static String get_First_Signer_Id() {
        String responseBody = list_Signers();
        if (responseBody == null) {
            System.out.println("No se pudo obtener la respuesta de los signers.");
            return null;
        }

        try {
            JSONArray results = new JSONObject(responseBody).getJSONArray("results");
            String signerId = results.getJSONObject(0).getJSONObject("content").getString("id");
            return signerId;

        } catch (Exception e) {
            System.out.println("Error al extraer el ID del primer signer");
            e.printStackTrace();
            return null;
        }
    }
}

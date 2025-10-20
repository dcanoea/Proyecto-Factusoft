package com.mycompany.pruebaFiskaly;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Organizations {

    public static void list_Organizations() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_MANAGEMENT_URL + "/organizations";
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

        } catch (Exception ex) {
            System.out.println("Error al listar clientes");
            ex.printStackTrace();
        }
    }

    public static void retrieve_Organization(String idCliente) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_MANAGEMENT_URL + "/organizations/" + idCliente;
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

        } catch (Exception ex) {
            System.out.println("Error al listar clientes");
            ex.printStackTrace();
        }
    }
}

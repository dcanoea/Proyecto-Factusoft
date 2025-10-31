package com.mycompany.pruebaFiskaly;

import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Organizations {

    // Recupera las organizaciones (distintas sedes) de la empresa
    public static void listOrganizations() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_MANAGEMENT_URL + "/organizations";
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

        } catch (Exception ex) {
            System.out.println("Error al listar clientes");
            ex.printStackTrace();
        }
    }

    // Devuelve organization asociada a un client
    public static void retrieveOrganization(String clientID) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_MANAGEMENT_URL + "/organizations/" + clientID;
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

        } catch (Exception ex) {
            System.out.println("Error al listar clientes");
            ex.printStackTrace();
        }
    }
}

package com.mycompany.pruebaFiskaly;

import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Organizations {

    // Recupera las organizaciones (distintas sedes) de la empresa
    public static String listOrganizations() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_MANAGEMENT_URL + Config.ORGANIZATIONS;
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

            return responseBody;
        } catch (Exception ex) {
            System.out.println("Error al listar clientes");
            ex.printStackTrace();
            return null;
        }
    }

    public static String getFirstOrganizationID() {
        String response = listOrganizations();
        if (response == null) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(response);
            JSONArray data = json.getJSONArray("data");
            if (data.length() > 0) {
                return data.getJSONObject(0).getString("_id");
            }
        } catch (Exception e) {
            System.out.println("Error al extraer el ID de la organización");
            e.printStackTrace();
        }
        return null;
    }

}

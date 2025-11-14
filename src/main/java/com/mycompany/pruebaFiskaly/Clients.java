package com.mycompany.pruebaFiskaly;

import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

//CLIENTE IDENTIFICA DE FORMA ÚNICA UN DISPOSITIVO TPV, APLICACIÓN U OTRO DISPOSITIVO UTILIZADO PARA EMITIR FACTURAS
public class Clients {

    // Crea un UUID único que referencia a un equipo/sistema/TPV/POS donde se hacen facturas
    public static void createClient() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + Config.CREATE_CLIENT;
            String token = Authentication.retrieveToken();

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
            System.out.println("Error al crear el client");
            e.printStackTrace();
        }
    }

    // Lista todos los clients
    public static String listClients() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.CLIENTS);

            return ConnectionAPI.requestAPI(client, get);

        } catch (Exception e) {
            return "Error al recuperar los clients";
        }
    }

    // Recupera el ID del primer client (pago por client, suele haber uno)
    public static String getFirstClientID() {
        String responseBody = listClients();
        if (responseBody == null) {
            System.out.println("No se pudo obtener la respuesta de los clients.");
            return null;
        }

        try {
            JSONArray results = new JSONObject(responseBody).getJSONArray("results");
            String signerId = results.getJSONObject(0).getJSONObject("content").getString("id");
            return signerId;

        } catch (Exception e) {
            System.out.println("Error al extraer el ID del primer client");
            e.printStackTrace();
            return null;
        }
    }
}

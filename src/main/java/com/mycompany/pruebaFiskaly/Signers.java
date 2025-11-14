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

public class Signers {

    // Representa un dispositivo que firma las facturas. 
    //Fiskaly usa por defecto su propio certificado electrónico como colaborador.
    public static void createSigner() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            UUID uuid = UUID.randomUUID();
            String url = Config.BASE_URL + Config.CREATE_SIGNER;
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
            System.out.println("Error al crear el signer");
            e.printStackTrace();
        }
    }

    // Lista Firmantes
    public static String listSigners() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.SIGNERS);

            String responseBody = ConnectionAPI.requestAPI(client, get);

            return responseBody;

        } catch (Exception e) {
            System.out.println("Error al recuperar los signers");
            e.printStackTrace();
            return null;
        }
    }

    // Recupera el primer firmante (sólo existe uno)
    public static String getFirstSignerID() {
        String responseBody = listSigners();
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

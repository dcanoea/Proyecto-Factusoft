package com.mycompany.pruebaFiskaly;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

public class ConnectionAPI {

    public static HttpPut putRequest(String endPoint, String body) throws IOException, JSONException {
        // ========= PETICIÓN API=========
        String token = Authentication.retrieveToken();
        String url = Config.BASE_URL + endPoint;
        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", "application/json");
        put.setHeader("Authorization", "Bearer " + token);
        put.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        System.out.println("Body envíado a la API");
        System.out.println(body);
        return put;
    }

    public static HttpGet getRequest(String endPoint) throws IOException, JSONException {
        // ========= PETICIÓN API=========
        String token = Authentication.retrieveToken();
        String url = Config.BASE_URL + endPoint;
        HttpGet get = new HttpGet(url);
        get.setHeader("Content-Type", "application/json");
        get.setHeader("Authorization", "Bearer " + token);
        return get;
    }

    public static String requestAPI(CloseableHttpClient client, HttpUriRequest request) throws IOException {
        // ========= RESPUESTA API=========
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println("C\u00f3digo de respuesta: " + statusCode);
        System.out.println("Respuesta completa del servidor:");
        
        //Formatear impresión por consola del JSON
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(responseBody, Object.class);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        String prettyJson = writer.writeValueAsString(json);
        System.out.println(prettyJson);
        
        return responseBody;
    }
}

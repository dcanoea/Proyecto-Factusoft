package com.mycompany.pruebaFiskaly;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Authentication {

    public static String retrieveToken() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + "/auth";

            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");

            JSONObject content = new JSONObject();
            content.put("api_key", Config.API_KEY);
            content.put("api_secret", Config.API_SECRET);

            JSONObject body = new JSONObject();
            body.put("content", content);

            StringEntity entity = new StringEntity(body.toString(), StandardCharsets.UTF_8);
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            // Extraer el bearer token del JSON
            String token = null;
            String marker = "\"bearer\":\"";
            int start = responseBody.indexOf(marker);
            if (start != -1) {
                start += marker.length();
                int end = responseBody.indexOf("\"", start);
                if (end != -1) {
                    token = responseBody.substring(start, end);
                }
            }
            return token;
        }
    }
}

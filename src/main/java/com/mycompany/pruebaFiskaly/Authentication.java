package com.mycompany.pruebaFiskaly;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Authentication {

    public static String retrieve_token() throws IOException {
        URL url = new URL(Config.BASE_URL + "/auth");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = String.format(
                "{\"content\": {\"api_key\": \"%s\", \"api_secret\": \"%s\"}}",
                Config.API_KEY, Config.API_SECRET
        );

        OutputStream os = connection.getOutputStream();
        os.write(jsonInputString.getBytes("utf-8"));
        os.flush();
        os.close();

        InputStream responseStream;
        try {
            responseStream = connection.getInputStream();
        } catch (IOException e) {
            responseStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Extraer el bearer token del JSON
        String token = null;
        String marker = "\"bearer\":\"";
        int start = response.indexOf(marker);
        if (start != -1) {
            start += marker.length();
            int end = response.indexOf("\"", start);
            if (end != -1) {
                token = response.substring(start, end);
            }
        }

        return token;
    }
}

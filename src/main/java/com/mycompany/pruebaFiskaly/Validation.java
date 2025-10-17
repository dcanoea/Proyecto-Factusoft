package com.mycompany.pruebaFiskaly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Validation {
    
    // AEAT no tiene entorno de pruebas, verificar en modo LIVE para confirmar las respuestas de la API
    public static String validate_AEAT(String nif) {
        String validation = null;
        try {
            // Crea un objeto URL con la dirección base (Config.BASE_URL) más el endpoint /validation/tin
            URL url = new URL(Config.BASE_URL + "/validation/tin");

            // Obtiene el token desde Authentication.Recuperar_token
            String token = Authentication.retrieve_token();

            // Abre una conexión HTTP con la URL especificada
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Establece el método HTTP como POST
            connection.setRequestMethod("POST");

            // Agrega el encabezado de autorización
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Indica que el cuerpo de la solicitud será JSON
            connection.setRequestProperty("Content-Type", "application/json");

            // Habilita el envío de datos en el cuerpo de la solicitud
            connection.setDoOutput(true);

            // Construye el cuerpo JSON con las credenciales necesarias (api_key y api_secret)
            String jsonInputString = String.format(
                    "{\"content\": {\"type\": \"AEAT\", \"entries\": [{\"tin\": \"%s\"}]}}",
                    nif
            );

            // Abre el flujo de salida para enviar el cuerpo de la solicitud
            OutputStream os = connection.getOutputStream();
            os.write(jsonInputString.getBytes("utf-8")); // Escribe el JSON en formato UTF-8
            os.flush(); // Fuerza el envío de los datos
            os.close(); // Cierra el flujo de salida

            // Intenta obtener el flujo de entrada con la respuesta del servidor
            InputStream responseStream;
            try {
                responseStream = connection.getInputStream(); // Si la respuesta es exitosa
            } catch (IOException e) {
                responseStream = connection.getErrorStream(); // Si hay error
            }

            // Lee la respuesta del servidor línea por línea
            BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine); // Acumula cada línea en el StringBuilder
            }
            in.close(); // Cierra el lector

            // Extrae el valor de validación del JSON de respuesta usando búsqueda de texto
            String marker = "\"result\":\""; // Patrón que precede al valor del token
            int start = response.indexOf(marker); // Busca la posición inicial del patrón
            if (start != -1) {
                start += marker.length(); // Avanza hasta el inicio del valor
                int end = response.indexOf("\"", start); // Busca el cierre de comillas
                if (end != -1) {
                    validation = response.substring(start, end); // Extrae el token
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return validation;
    }

    public static boolean validate_VIES(String country_code, String nif) {
        String validation = null;
        try {
            // Crea un objeto URL con la dirección base (Config.BASE_URL) más el endpoint /validation/tin
            URL url = new URL(Config.BASE_URL + "/validation/tin");

            // Obtiene el token desde Authentication.Recuperar_token
            String token = Authentication.retrieve_token();

            // Abre una conexión HTTP con la URL especificada
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Establece el método HTTP como POST
            connection.setRequestMethod("POST");

            // Agrega el encabezado de autorización
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Indica que el cuerpo de la solicitud será JSON
            connection.setRequestProperty("Content-Type", "application/json");

            // Habilita el envío de datos en el cuerpo de la solicitud
            connection.setDoOutput(true);

            // Construye el cuerpo JSON con las credenciales necesarias (api_key y api_secret)
            String jsonInputString = String.format(
                    "{\"content\": {\"type\": \"VIES\", \"tin\": \"%s\", \"country_code\": \"%s\"}}",
                    nif, country_code
            );

            // Abre el flujo de salida para enviar el cuerpo de la solicitud
            OutputStream os = connection.getOutputStream();
            os.write(jsonInputString.getBytes("utf-8")); // Escribe el JSON en formato UTF-8
            os.flush(); // Fuerza el envío de los datos
            os.close(); // Cierra el flujo de salida

            // Intenta obtener el flujo de entrada con la respuesta del servidor
            InputStream responseStream;
            try {
                responseStream = connection.getInputStream(); // Si la respuesta es exitosa
            } catch (IOException e) {
                responseStream = connection.getErrorStream(); // Si hay error
            }

            // Lee la respuesta del servidor línea por línea
            BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine); // Acumula cada línea en el StringBuilder
            }
            in.close(); // Cierra el lector

            // Extrae el valor de validación del JSON de respuesta usando búsqueda de texto
            String marker = "\"result\":\""; // Patrón que precede al valor del token
            int start = response.indexOf(marker); // Busca la posición inicial del patrón
            if (start != -1) {
                start += marker.length(); // Avanza hasta el inicio del valor
                int end = response.indexOf("\"", start); // Busca el cierre de comillas
                if (end != -1) {
                    validation = response.substring(start, end); // Extrae el token
                }
            }
            
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }     
        
        return validation.equals("VALID");
    }
}

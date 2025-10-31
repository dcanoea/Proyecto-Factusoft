package com.mycompany.pruebaFiskaly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Validation {

    // MÉTODO PARA VALIDAR NIF/CIF EN AEAT (AMBITO FISCAL ESPAÑOL)
    // AEAT no tiene entorno de pruebas, verificar en modo LIVE para confirmar las respuestas de la API
    public static String validateAEAT(String nif) {
        String validation = null;
        try {
            // Crea un objeto URL con la dirección base (Config.BASE_URL) más el endpoint /validation/tin
            URL url = new URL(Config.BASE_URL + "/validation/tin");

            // Obtiene el token desde Authentication.Recuperar_token
            String token = Authentication.retrieveToken();

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

    // MÉTODO PARA VALIDAR NIF/CIF EN VIES (OPERADORES INTRACOMUNITARIOS EN LA UE)
    public static boolean validateVIES(String country_code, String nif) {
        try {
            // Construye la URL del endpoint de validación TIN
            URL url = new URL(Config.BASE_URL + "/validation/tin");
            // Recupera el token de autenticación para la API
            String token = Authentication.retrieveToken();
            // Abre la conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Define el método HTTP como POST
            connection.setRequestMethod("POST");
            // Añade el encabezado de autorización con el token Bearer
            connection.setRequestProperty("Authorization", "Bearer " + token);
            // Indica que el contenido enviado será JSON
            connection.setRequestProperty("Content-Type", "application/json");
            // Permite enviar datos en el cuerpo de la solicitud
            connection.setDoOutput(true);

            // Normaliza el NIF y el código de país (mayúsculas y sin espacios)
            String jsonInputString = String.format(
                    "{\"content\": {\"type\": \"VIES\", \"tin\": \"%s\", \"country_code\": \"%s\"}}",
                    nif.trim().toUpperCase(), country_code.trim().toUpperCase()
            );

            // Escribe el cuerpo JSON en el flujo de salida
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                os.flush(); // Fuerza el envío
            }

            // Intenta obtener el flujo de respuesta (si hay error, usa el flujo de error)
            InputStream responseStream;
            try {
                responseStream = connection.getInputStream(); // Respuesta OK
            } catch (IOException e) {
                responseStream = connection.getErrorStream(); // Respuesta con error
            }

            // Lee la respuesta del servidor línea por línea
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line); // Acumula el contenido
                }
            }

            // Parsea la respuesta JSON
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Extrae el array "results" del JSON
            JSONArray resultsArray = jsonResponse.optJSONArray("results");

            // Si hay al menos un resultado, accede al objeto "content"
            if (resultsArray != null && resultsArray.length() > 0) {
                JSONObject content = resultsArray.getJSONObject(0).optJSONObject("content");

                // Extrae el campo "result" dentro de "content"
                String result = content != null ? content.optString("result", "") : "";

                // Devuelve true si el resultado es "VALID"
                return "VALID".equals(result);
            }

        } catch (Exception e) {
            // Imprime cualquier excepción que ocurra
            e.printStackTrace();
        }

        // Si algo falla o no hay resultado, devuelve false
        return false;
    }

    // MÉTODO PARA VALIDAR CIF/NIF EN MASA DE LA BBDD DE LA EMPRESA
    public static ArrayList<Cliente> validarListaNIF(JSONObject inputJson) {
        ArrayList<Cliente> clientesNoValidados = new ArrayList<>();

        JSONArray results = inputJson.optJSONArray("results");
        
        
        return clientesNoValidados;
    }

    public static class Cliente {

        private String nombre;
        private String apellidos;
        private String NIF;
        private boolean validado;

        public Cliente(String nombre, String apellidos, String NIF, boolean validado) {
            this.nombre = nombre;
            this.apellidos = apellidos;
            this.NIF = NIF;
            this.validado = validado;
        }

        @Override
        public String toString() {
            return "Cliente{" + "nombre=" + nombre + ", apellidos=" + apellidos + ", NIF=" + NIF + ", validado=" + validado + '}';
        }

        public boolean isValidado() {
            return validado;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellidos() {
            return apellidos;
        }

        public String getNIF() {
            return NIF;
        }
    }
}

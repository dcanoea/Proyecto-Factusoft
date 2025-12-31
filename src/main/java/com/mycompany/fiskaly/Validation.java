package com.mycompany.fiskaly;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Validation {

    // MÉTODO PARA VALIDAR NIF/CIF/NIE SEGÚN CÁLCULO DE DIGITOS DE CONTROL
    public static boolean validateTaxID(String nif) {
        if (nif == null) {
            return false;
        }

        // Eliminar cualquier carácter que no sea letra o número
        nif = nif.toUpperCase().replaceAll("[^A-Z0-9]", "");

        if (nif.length() != 9) {
            return false;
        }

        char first = nif.charAt(0);
        char last = nif.charAt(8);

        // Validar NIF (8 dígitos + letra)
        if (Character.isDigit(first)) {
            String num = nif.substring(0, 8);
            if (!num.matches("\\d{8}")) {
                return false;
            }
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int index = Integer.parseInt(num) % 23;
            return last == letras.charAt(index);
        }

        // Validar NIE (X/Y/Z + 7 dígitos + letra)
        if ("XYZ".indexOf(first) >= 0) {
            String nieNum = nif.replaceFirst("X", "0").replaceFirst("Y", "1").replaceFirst("Z", "2");
            String num = nieNum.substring(0, 8);
            if (!num.matches("\\d{8}")) {
                return false;
            }
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int index = Integer.parseInt(num) % 23;
            return last == letras.charAt(index);
        }

        // Validar CIF (letra + 7 dígitos + letra/dígito)
        if ("ABCDEFGHJKLMNPQRSUVW".indexOf(first) >= 0) {
            String digits = nif.substring(1, 8);
            if (!digits.matches("\\d{7}")) {
                return false;
            }

            int sumaPar = 0, sumaImpar = 0;
            for (int i = 0; i < digits.length(); i++) {
                int n = Character.getNumericValue(digits.charAt(i));
                if ((i + 1) % 2 == 0) {
                    sumaPar += n;
                } else {
                    int doble = n * 2;
                    sumaImpar += doble > 9 ? doble - 9 : doble;
                }
            }

            int total = sumaPar + sumaImpar;
            int controlNum = (10 - (total % 10)) % 10;
            char controlChar = "JABCDEFGHI".charAt(controlNum);

            if ("ABEH".indexOf(first) >= 0) {
                return last == Character.forDigit(controlNum, 10);
            }
            if ("KPQS".indexOf(first) >= 0) {
                return last == controlChar;
            }
            return last == controlChar || last == Character.forDigit(controlNum, 10);
        }

        return false;
    }

    // MÉTODO PARA VALIDAR NIF/CIF EN AEAT (AMBITO FISCAL ESPAÑOL)
    // AEAT no tiene entorno de pruebas, verificar en modo LIVE para confirmar las respuestas de la API
    public static String validateAEAT(String nif) {
        String validation = null;
        try (CloseableHttpClient client = org.apache.http.impl.client.HttpClients.createDefault()) {
            // Construye el cuerpo JSON que espera la API AEAT
            String jsonBody = String.format(
                    "{\"content\": {\"type\": \"AEAT\", \"entries\": [{\"tin\": \"%s\"}]}}",
                    nif
            );

            HttpPost request = ConnectionAPI.postRequest(Config.VALIDATION_TIN);

            // Añade el cuerpo a la petición
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            // Envía la solicitud y obtiene la respuesta como String
            String responseBody = ConnectionAPI.requestAPI(client, request);

            // Extrae el campo "result" del JSON de respuesta
            String marker = "\"result\":\"";
            int start = responseBody.indexOf(marker);
            if (start != -1) {
                start += marker.length();
                int end = responseBody.indexOf("\"", start);
                if (end != -1) {
                    validation = responseBody.substring(start, end);
                }
            }
        } catch (IOException | JSONException e) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, e);
        }

        return validation;
    }

    // MÉTODO PARA VALIDAR NIF/CIF EN VIES (OPERADORES INTRACOMUNITARIOS EN LA UE)
    public static String validateVIES(String country_code, String nif) {
        String validation = null;
        try (CloseableHttpClient client = org.apache.http.impl.client.HttpClients.createDefault()) {
            // Cuerpo para la petición a la API
            String jsonBody = String.format(
                    "{\"content\": {\"type\": \"VIES\", \"tin\": \"%s\", \"country_code\": \"%s\"}}",
                    nif.trim().toUpperCase(), country_code.trim().toUpperCase()
            );

            HttpPost request = ConnectionAPI.postRequest(Config.VALIDATION_TIN);

            // Añade el cuerpo a la petición
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            // Envía la solicitud y obtiene la respuesta como String
            String responseBody = ConnectionAPI.requestAPI(client, request);

            // Extrae el campo "result" del JSON de respuesta
            String marker = "\"result\":\"";
            int start = responseBody.indexOf(marker);
            if (start != -1) {
                start += marker.length();
                int end = responseBody.indexOf("\"", start);
                if (end != -1) {
                    validation = responseBody.substring(start, end);
                }
            }
        } catch (IOException | JSONException e) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, e);
        }
        return validation;
    }

    // MÉTODO PARA VALIDAR CIF/NIF EN MASA DE LA BBDD DE LA EMPRESA
    public static ArrayList<Cliente> validarListaNif() {
        ArrayList<Cliente> noValidados = new ArrayList<>();
        int count = 0;
        try {
            //Ruta al archivo JSON
            String rutaJSON = "C:\\Users\\user\\Desktop\\clientes_export.json";
            //Leer contenido del JSON antes de poder convertirlo a un JSONObject
            String contenido = new String(Files.readAllBytes(Paths.get(rutaJSON)));
            //Convertir texto del contenido a JSONObject
            JSONObject json = new JSONObject(contenido);

            //Recorrer JSON 
            JSONArray results = json.optJSONArray("results");
            for (Object resultObject : results) {
                JSONObject result = (JSONObject) resultObject;
                JSONArray items = result.getJSONArray("items");
                for (Object itemObject : items) {
                    JSONObject item = (JSONObject) itemObject;

                    int codigo = item.getInt("codigo");
                    int numero = item.getInt("numero");
                    String nombre = item.getString("nombre_fiscal");
                    String apellidos = item.getString("nombre_comercial");
                    String nif = item.getString("nif");

                    boolean nifValido = validateTaxID(nif);
                    {
                        if (!nifValido) {
                            Cliente cliente = new Cliente(codigo, numero, nombre, apellidos, nif, nifValido);
                            noValidados.add(cliente);
                            count++;
                        }
                    }
                }
            }

            System.out.println("Total no validados: " + count);
        } catch (IOException ex) {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return noValidados;

    }

    public static class Cliente {

        private int codigo;
        private int numero;
        private String nombre;
        private String apellidos;
        private String NIF;
        private boolean validado;

        public Cliente(int codigo, int numero, String nombre, String apellidos, String NIF, boolean validado) {
            this.codigo = codigo;
            this.numero = numero;
            this.nombre = nombre;
            this.apellidos = apellidos;
            this.NIF = NIF;
            this.validado = validado;
        }

        public int getCodigo() {
            return codigo;
        }

        public int getNumero() {
            return numero;
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

        public boolean isValidado() {
            return validado;
        }

        @Override
        public String toString() {
            return "Cliente{" + "codigo=" + codigo + ", numero=" + numero + ", nombre=" + nombre + ", apellidos=" + apellidos + ", NIF=" + NIF + ", validado=" + validado + '}';
        }

    }
}

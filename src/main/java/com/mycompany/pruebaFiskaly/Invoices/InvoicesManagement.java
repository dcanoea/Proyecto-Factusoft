package com.mycompany.pruebaFiskaly.Invoices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.pruebaFiskaly.Config;
import com.mycompany.pruebaFiskaly.ConnectionAPI;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class InvoicesManagement {

    // Este endpoint obtiene una lista de las facturas emitidas desde un dispositivo cliente.
    public static void listInvoices() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.INVOICES);
 
            System.out.println(ConnectionAPI.requestAPI(client, get));
            
        } catch (Exception e) {
            System.out.println("Error al recuperar los clients");
            e.printStackTrace();
        }
    }

    // Obtiene los detalles de una factura
    public static JSONObject retrieveInvoice(String invoiceID) {
        JSONObject factura = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.INVOICES + "/" + invoiceID);

            String responseBody = ConnectionAPI.requestAPI(client, get);

            JSONObject json = new JSONObject(responseBody);
            if (json.has("content")) {
                factura = json.getJSONObject("content");
            } else {
                System.err.println("No se encontró el contenido de la factura: " + invoiceID);
            }
            System.out.println(json.toString(5));

        } catch (Exception e) {
            System.err.println("Error al recuperar la factura: " + invoiceID);
            e.printStackTrace();
        }
        return factura;
    }

    // OBTIENE Nº DE FACTURA MEDIANTE ID DE FACTURA
    public static String getInvoiceNumberByID(String invoiceID) {
        String invoiceNumber = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.INVOICES + "/" + invoiceID);

            String responseBody = ConnectionAPI.requestAPI(client, get);
            
            JSONObject json = new JSONObject(responseBody);
            if (!json.has("content")) {
                System.err.println("No se encontró el contenido de la factura: " + invoiceID);
                return null;
            }

            // Extraer el campo "data" como string
            String innerJsonString = json.getJSONObject("content").optString("data", "");
            if (innerJsonString.isEmpty()) {
                System.err.println("El campo 'data' está vacío.");
                return null;
            }

            // Parsear el string como JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode innerJson = mapper.readTree(innerJsonString);

            // Extraer el número de factura desde innerJson.data.number
            invoiceNumber = innerJson.path("data").path("number").asText();

        } catch (Exception e) {
            System.err.println("Error al recuperar el número de factura: " + invoiceID);
            e.printStackTrace();
        }
        return invoiceNumber;
    }

    // Obtiene id factura filtrando por nº de factura
    public static String getInvoiceIDByNumber(String number) {
        String invoiceID = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest("/invoices?number=" + number);

            String responseBody = ConnectionAPI.requestAPI(client, get);

            JSONObject json = new JSONObject(responseBody);

            JSONArray results = json.getJSONArray("results");
            JSONObject content = results.getJSONObject(0).getJSONObject("content");
            invoiceID = content.getString("id");

        } catch (Exception e) {
            System.out.println("Error al recuperar la factura");
            e.printStackTrace();
        }
        return invoiceID;
    }

    // Obtiene el estado de registro y descripcion de una factura (para gestionar posibles correcciones)
    public static void getRegistrationDescription(String invoiceNumber) {
        String invoiceID = getInvoiceIDByNumber(invoiceNumber);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.INVOICES + "/" + invoiceID);

            String responseBody = ConnectionAPI.requestAPI(client, get);

            JSONObject json = new JSONObject(responseBody);
            if (!json.has("content")) {
                System.err.println("No se encontró el contenido de la factura: " + invoiceID);
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json.toString());

            // Estado de registro
            String registration = root.path("content").path("transmission").path("registration").asText();
            System.out.println("Estado de registro de la factura " + invoiceNumber + ": " + registration);

            // Validaciones
            JsonNode validations = root.path("content").path("validations");
            if (validations.isArray() && validations.size() > 0) {
                for (JsonNode error : validations) {
                    String code = error.path("code").asText("Sin código");
                    String message = error.path("description").asText("Sin descripción");
                    System.out.println("Código: " + code);
                    System.out.println("Descripción: " + message);
                }
            } else {
                System.out.println("No se encontraron errores de validación.");
            }

        } catch (Exception e) {
            System.err.println("Error al inspeccionar la factura: " + invoiceID);
            e.printStackTrace();
        }
    }

    public static String getInvoiceFullAmount(String invoiceNumber) {
        String invoiceID = getInvoiceIDByNumber(invoiceNumber);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.INVOICES + "/" + invoiceID);   
            
            String responseBody = ConnectionAPI.requestAPI(client, get);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            // Extraer JSON interno (string escapado dentro de content.data)
            String escapedInnerJson = root.path("content").path("data").asText();

            // Desescapar correctamente usando JSONObject
            JSONObject innerJsonObject = new JSONObject(escapedInnerJson);

            // Parsear con Jackson
            JsonNode innerNode = mapper.readTree(innerJsonObject.toString());

            // Obtener el total full_amount
            String fullAmount = innerNode.path("data").path("full_amount").asText();
            System.out.println("Importe total de la factura " + invoiceNumber + ": " + fullAmount);
            return fullAmount;
        } catch (Exception e) {
            System.err.println("Error al recuperar la factura: " + invoiceID);
            return null;
        }
    }
}

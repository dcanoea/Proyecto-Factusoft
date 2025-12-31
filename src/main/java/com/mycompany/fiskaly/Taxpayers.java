package com.mycompany.fiskaly;

import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Taxpayers {

    // Crear contribuyente (persona o empresa que emite facturas)
    public static void createTaxpayer(String legal_name, String tax_number, String territory) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + Config.TAXPAYER;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // Construir el objeto issuer
            JSONObject issuer = new JSONObject();
            issuer.put("legal_name", legal_name);
            issuer.put("tax_number", tax_number);

            // Construir el objeto content
            JSONObject content = new JSONObject();
            content.put("issuer", issuer);
            content.put("territory", territory);

            // Construir el cuerpo final
            JSONObject body = new JSONObject();
            body.put("content", content);

            // Enviar solicitud
            StringEntity entity = new StringEntity(body.toString(), StandardCharsets.UTF_8);
            put.setEntity(entity);

            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);

        } catch (Exception e) {
            System.out.println("Error al crear el taxpayer");
            e.printStackTrace();
        }
    }

    // Recupera contribuyente
    public static JSONObject retrieveTaxpayer() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = ConnectionAPI.getRequest(Config.TAXPAYER);
            
            String responseBody = ConnectionAPI.requestAPI(client, get);

            return new JSONObject(responseBody);

        } catch (Exception e) {
            System.out.println("Error al recuperar el contribuyente");
            e.printStackTrace();
            return null;
        }
    }

    // MÉTODO PARA ACTUALIZAR CONTRIBUYENTE. SI SE PASA UN PARAMETRO NULL O "" LO IGNORA
    public static void updateTaxpayer(boolean deactivate, String legal_name, String tax_number, String municipality,
            String city, String street, String postal_code, String number, String country_code, String email, String type,
            String phone, String website, String contact_person, String vat_number, String registration_number,
            String fiscal_year, String industry, String language, String timezone, String notes) {

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + Config.TAXPAYER;
            String token = Authentication.retrieveToken();

            HttpPatch patch = new HttpPatch(url);
            patch.setHeader("Content-Type", "application/json");
            patch.setHeader("Authorization", "Bearer " + token);

            // Construir metadata eliminando claves vacías
            JSONObject metadata = new JSONObject();
            if (legal_name != null && !legal_name.isEmpty()) {
                metadata.put("legal_name", legal_name);
            }
            if (tax_number != null && !tax_number.isEmpty()) {
                metadata.put("tax_number", tax_number);
            }
            if (municipality != null && !municipality.isEmpty()) {
                metadata.put("municipality", municipality);
            }
            if (city != null && !city.isEmpty()) {
                metadata.put("city", city);
            }
            if (street != null && !street.isEmpty()) {
                metadata.put("street", street);
            }
            if (postal_code != null && !postal_code.isEmpty()) {
                metadata.put("postal_code", postal_code);
            }
            if (number != null && !number.isEmpty()) {
                metadata.put("number", number);
            }
            if (country_code != null && !country_code.isEmpty()) {
                metadata.put("country_code", country_code);
            }
            if (email != null && !email.isEmpty()) {
                metadata.put("email", email);
            }
            if (type != null && !type.isEmpty()) {
                metadata.put("type", type);
            }
            if (phone != null && !phone.isEmpty()) {
                metadata.put("phone", phone);
            }
            if (website != null && !website.isEmpty()) {
                metadata.put("website", website);
            }
            if (contact_person != null && !contact_person.isEmpty()) {
                metadata.put("contact_person", contact_person);
            }
            if (vat_number != null && !vat_number.isEmpty()) {
                metadata.put("vat_number", vat_number);
            }
            if (registration_number != null && !registration_number.isEmpty()) {
                metadata.put("registration_number", registration_number);
            }
            if (fiscal_year != null && !fiscal_year.isEmpty()) {
                metadata.put("fiscal_year", fiscal_year);
            }
            if (industry != null && !industry.isEmpty()) {
                metadata.put("industry", industry);
            }
            if (language != null && !language.isEmpty()) {
                metadata.put("language", language);
            }
            if (timezone != null && !timezone.isEmpty()) {
                metadata.put("timezone", timezone);
            }
            if (notes != null && !notes.isEmpty()) {
                metadata.put("notes", notes);
            }

            // Construir cuerpo raíz (sin content)
            JSONObject body = new JSONObject();
            body.put("metadata", metadata);
            if (deactivate) {
                body.put("state", "DISABLED");
            }

            StringEntity entity = new StringEntity(body.toString(), StandardCharsets.UTF_8);
            patch.setEntity(entity);

            HttpResponse response = client.execute(patch);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);

        } catch (Exception e) {
            System.out.println("Error al actualizar el taxpayer");
            e.printStackTrace();
        }
    }
}

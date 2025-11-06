package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Config;
import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ContentCorrectingDTO {

    public String type = "CORRECTING";
    public Method method;
    public Code code;
    public String id; //id de factura que se va a rectificar
    public InvoiceDTO invoice;

    public enum Method {
        SUBSTITUTION, DIFFERENCES
    }

    public enum Code {
        CORRECTION_1, CORRECTION_2, CORRECTION_3, CORRECTION_4
    }

    public ContentCorrectingDTO() {
    }

    public ContentCorrectingDTO(Method method, Code code, String originalInvoiceNumber, InvoiceDTO invoice) {
        this.method = method;
        this.code = code;
        this.id = getInvoiceIDByNumber(originalInvoiceNumber);
        this.invoice = invoice;
    }

    public String getId() {
        return id;
    }
    
    // Obtiene id factura filtrando por nº de factura
    public static String getInvoiceIDByNumber(String number) {
        String invoiceID = null;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = Config.BASE_URL + "/invoices?number=" + number;
            String token = Authentication.retrieveToken();

            HttpGet get = new HttpGet(url);
            get.setHeader("Content-Type", "application/json");
            get.setHeader("Authorization", "Bearer " + token);

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
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

}

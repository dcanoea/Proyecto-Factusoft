/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.fiskaly.Invoices.Respuestas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author user
 */
public class testRespuestaAPI {

    public testRespuestaAPI() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testApiResponseCompletaSegura() throws Exception {
        String path = "src/test/java/com/mycompany/pruebaFiskaly/Invoices/Respuestas/JSON/Complete.json";
        String json = new String(Files.readAllBytes(Paths.get(path)));
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        // --- content ---
        JsonNode content = root.get("content");
        assertNotNull(content, "Falta 'content'");

        // --- transmission ---
        JsonNode transmission = content.get("transmission");
        assertNotNull(transmission, "Falta 'content.transmission'");
        assertEquals("NOT_CANCELLED", transmission.path("cancellation").asText());
        assertEquals("REGISTERED", transmission.path("registration").asText());

        // --- data (es un string JSON) ---
        String dataJsonString = content.path("data").asText();
        assertFalse(dataJsonString.isEmpty(), "Falta 'content.data'");
        JsonNode dataNode = mapper.readTree(dataJsonString);
        JsonNode data = dataNode.path("data");
        assertNotNull(data, "Falta 'data.data'");

        assertEquals("Factura Completa", data.path("text").asText());
        assertEquals("SIMPLIFIED", data.path("type").asText());
        assertEquals("20251115", data.path("number").asText());
        assertEquals("1256.30", data.path("full_amount").asText());

        // --- items ---
        JsonNode items = data.path("items");
        assertTrue(items.isArray(), "'items' debe ser un array");

        for (JsonNode item : items) {
            assertNotNull(item.path("text").asText(), "Falta 'item.text'");
            assertNotNull(item.path("unit_amount").asText(), "Falta 'item.unit_amount'");
            assertNotNull(item.path("full_amount").asText(), "Falta 'item.full_amount'");
            assertNotNull(item.path("quantity").asText(), "Falta 'item.quantity'");
            assertNotNull(item.path("vat_type").asText(), "Falta 'item.vat_type'");

            if (item.has("discount") && !item.path("discount").isNull()) {
                assertNotNull(item.path("discount").asText(), "Falta 'item.discount'");
            }

            JsonNode system = item.path("system");
            assertNotNull(system, "Falta 'item.system'");
            assertNotNull(system.path("type").asText(), "Falta 'item.system.type'");

            JsonNode category = system.path("category");
            assertNotNull(category, "Falta 'item.system.category'");
            assertNotNull(category.path("type").asText(), "Falta 'category.type'");
            String type = category.path("type").asText();
            if ("VAT".equals(type)) {
                assertNotNull(category.path("rate").asText(), "Falta 'category.rate'");
            } else if ("NO_VAT".equals(type)) {
                assertNotNull(category.path("cause").asText(), "Falta 'category.cause'");
            }
        }

        // --- recipients ---
        JsonNode recipients = data.path("recipients");
        for (JsonNode recipient : recipients) {
            JsonNode id = recipient.path("id");
            assertNotNull(id, "Falta 'recipient.id'");
            assertNotNull(id.path("legal_name").asText(), "Falta 'recipient.id.legal_name'");
            assertNotNull(id.path("tax_number").asText(), "Falta 'recipient.id.tax_number'");
            assertNotNull(id.path("registered").asBoolean(), "Falta 'recipient.id.registered'");
            assertNotNull(recipient.path("address_line").asText(), "Falta 'recipient.address_line'");
            assertNotNull(recipient.path("postal_code").asText(), "Falta 'recipient.postal_code'");
        }

        // --- compliance ---
        JsonNode compliance = content.path("compliance");
        assertNotNull(compliance, "Falta 'content.compliance'");
        assertNotNull(compliance.path("text").asText(), "Falta 'compliance.text'");
        assertNotNull(compliance.path("url").asText(), "Falta 'compliance.url'");

        JsonNode code = compliance.path("code");
        assertNotNull(code, "Falta 'compliance.code'");
        assertNotNull(code.path("type").asText(), "Falta 'compliance.code.type'");

        JsonNode image = code.path("image");
        assertNotNull(image, "Falta 'compliance.code.image'");
        assertNotNull(image.path("data").asText(), "Falta 'image.data'");
        assertNotNull(image.path("format").asText(), "Falta 'image.format'");

        JsonNode measurements = image.path("measurements");
        assertNotNull(measurements, "Falta 'image.measurements'");
        assertNotNull(measurements.path("unit").asText(), "Falta 'measurements.unit'");
        assertNotNull(measurements.path("width").asInt(), "Falta 'measurements.width'");
        assertNotNull(measurements.path("height").asInt(), "Falta 'measurements.height'");

        // --- client ---
        JsonNode client = content.path("client");
        assertNotNull(client, "Falta 'content.client'");
        assertNotNull(client.path("id").asText(), "Falta 'client.id'");

        // --- state y issued_at ---
        assertEquals("ISSUED", content.path("state").asText());
        assertNotNull(content.path("issued_at").asText(), "Falta 'issued_at'");

        // --- signer ---
        JsonNode signer = content.path("signer");
        assertNotNull(signer, "Falta 'content.signer'");
        assertNotNull(signer.path("id").asText(), "Falta 'signer.id'");

        // --- validations ---
        JsonNode validations = content.path("validations");
        assertTrue(validations.isArray(), "'validations' debe ser un array");
    }
}

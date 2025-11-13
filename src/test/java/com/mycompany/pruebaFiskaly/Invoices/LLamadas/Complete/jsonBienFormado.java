/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.LLamadas.Complete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ContentCompleteDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.DataDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ItemDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.RecipientsDTO;
import com.mycompany.pruebaFiskaly.Invoices.JsonUtil;
import java.io.IOException;
import java.net.MalformedURLException;
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
public class jsonBienFormado {

    public jsonBienFormado() {
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

    /**
     * Test of createInvoice method, of class CreateCompleteInvoice.
     */
    @Test
    public void testJsonBienFormado() throws JsonProcessingException, MalformedURLException, IOException {
        String path = "src/test/java/com/mycompany/pruebaFiskaly/Invoices/Llamadas/JSON/Complete.json";
        String json = new String(Files.readAllBytes(Paths.get(path)));

        // Extraer solo el objeto interno
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String jsonParaDTO = root.get("content").toString();

        // Deserializar al DTO
        ContentCompleteDTO content = JsonUtil.fromJson(jsonParaDTO, ContentCompleteDTO.class);

        // Verificar campos principales
        assertNotNull(content, "Falta el campo 'content'");
        assertNotNull(content.data, "Falta el campo 'data'");
        assertNotNull(content.recipients, "Falta el campo 'recipients'");
        assertNotNull(content.type, "Falta el campo 'type'");

        // Verificar datos de 'data'
        DataDTO data = content.data;
        assertNotNull(data.text, "Falta 'data.text'");
        assertNotNull(data.type, "Falta 'data.type'");
        assertNotNull(data.items, "Falta 'data.items'");
        assertNotNull(data.invoiceNumber, "Falta 'data.number'");
        assertNotNull(data.fullAmount, "Falta 'data.full_amount'");

        // Verificar primer item
        ItemDTO item = data.items.get(0);
        assertNotNull(item.quantity, "Falta 'item.quantity'");
        assertNotNull(item.system, "Falta 'item.system'");
        assertNotNull(item.text, "Falta 'item.text'");
        assertNotNull(item.unitAmount, "Falta 'item.unit_amount'");
        assertNotNull(item.vatype, "Falta 'item.vat_type'");
        assertNotNull(item.fullAmount, "Falta 'item.full_amount'");

        // Verificar system.category
        assertNotNull(item.system.category, "Falta 'item.system.category'");
        assertNotNull(item.system.category.type, "Falta 'category.type'");
        assertNotNull(item.system.category.rate, "Falta 'category.rate'");

        // Verificar primer recipient
        RecipientsDTO recipient = content.recipients.get(0);
        assertNotNull(recipient.id, "Falta 'recipient.id'");
        assertNotNull(recipient.id.taxNumber, "Falta 'recipient.id.tax_number'");
        assertNotNull(recipient.id.legalName, "Falta 'recipient.id.legal_name'");
        assertNotNull(recipient.addressLine, "Falta 'recipient.address_line'");
        assertNotNull(recipient.postalCode, "Falta 'recipient.postal_code'");
    }
}

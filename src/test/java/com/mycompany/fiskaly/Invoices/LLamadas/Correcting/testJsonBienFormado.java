/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.fiskaly.Invoices.LLamadas.Correcting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.fiskaly.Invoices.DTO.ContentCorrectingDTO;
import com.mycompany.fiskaly.Invoices.DTO.DataDTO;
import com.mycompany.fiskaly.Invoices.DTO.InvoiceDTO;
import com.mycompany.fiskaly.Invoices.DTO.ItemDTO;
import com.mycompany.fiskaly.Invoices.DTO.RecipientsDTO;
import com.mycompany.fiskaly.Invoices.JsonUtil;
import java.io.IOException;
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
public class testJsonBienFormado {

    public testJsonBienFormado() {
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
    public void testJsonBienFormado() throws IOException {
        String path = "src/test/java/com/mycompany/pruebaFiskaly/Invoices/Llamadas/JSON/Correcting.json";
        String json = new String(Files.readAllBytes(Paths.get(path)));

        // Extraer solo el objeto interno
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String jsonParaDTO = root.get("content").toString();

        // Deserializar JSON
        ContentCorrectingDTO content = JsonUtil.fromJson(jsonParaDTO, ContentCorrectingDTO.class);

        //Campos en "content"
        // Verificar campos principales
        assertNotNull(content, "Falta el campo 'content'");
        assertNotNull(content.type, "Falta el campo 'type'");
        assertNotNull(content.method, "Falta el campo 'method'");
        assertNotNull(content.code, "Falta el campo 'code'");
        assertNotNull(content.id, "Falta el campo 'id");
        assertNotNull(content.invoice, "Falta el campo 'invoice'");

        // Verificar datos de 'invoice'
        InvoiceDTO invoice = content.invoice;
        assertNotNull(invoice.type, "Falta 'invoice.type'");
        assertNotNull(invoice.data, "Falta 'invoice.data'");
        assertNotNull(invoice.recipients, "Falta 'invoice.recipients'");

        // Verificar datos de 'data'
        DataDTO data = content.invoice.data;
        assertNotNull(data.invoiceNumber, "Falta 'data.number'");
        assertNotNull(data.series, "Falta 'data.series'");
        assertNotNull(data.text, "Fatla 'data.text");
        assertNotNull(data.type, "Falta 'data.type");
        assertNotNull(data.items, "Falta 'data.items'");

        // Verificar items
        for (ItemDTO item : data.items) {
            assertNotNull(item.quantity, "Falta 'item.quantity'");
            assertNotNull(item.system, "Falta 'item.system'");
            if (item.discount != null)  {
                assertNotNull(item.discount, "Falta 'item.discount'");
            }
            assertNotNull(item.text, "Falta 'item.text'");
            assertNotNull(item.unitAmount, "Falta 'item.unit_amount'");
            assertNotNull(item.vatype, "Falta 'item.vat_type'");
            assertNotNull(item.fullAmount, "Falta 'item.full_amount'");

            // Verificar system.category
            assertNotNull(item.system, "Falta 'item.system'");
            assertNotNull(item.system.type, "Falta 'item.system.type");
            assertNotNull(item.system.category, "Falta 'item.system.category'");
            assertNotNull(item.system.category.type, "Falta 'category.type'");
            if (item.system.category.type.equals("VAT")) {
                assertNotNull(item.system.category.rate, "Falta 'category.rate'");
            }
            if (item.system.category.type.equals("NO_VAT")) {
                assertNotNull(item.system.category.cause, "Falta 'category.cause'");
            }
        }
        assertNotNull(data.fullAmount, "Falta 'data.full_amount'");

        // Verificar primer recipient
        RecipientsDTO recipient = content.invoice.recipients.get(0);
        assertNotNull(recipient.id, "Falta 'recipient.id'");
        assertNotNull(recipient.id.registered, "Falta 'recipient.id.registered");
        assertNotNull(recipient.id.taxNumber, "Falta 'recipient.id.tax_number'");
        assertNotNull(recipient.id.legalName, "Falta 'recipient.id.legal_name'");
        assertNotNull(recipient.addressLine, "Falta 'recipient.address_line'");
        assertNotNull(recipient.postalCode, "Falta 'recipient.postal_code'");
    }

}

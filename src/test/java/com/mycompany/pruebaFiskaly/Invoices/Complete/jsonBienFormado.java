/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.Complete;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ContentCompleteDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.DataDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ItemDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.RecipientsDTO;
import com.mycompany.pruebaFiskaly.Invoices.JsonUtil;
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
    public void testJsonBienFormado() throws JsonProcessingException {
        String json = "{ \"content\": {\n"
                + "  \"data\" : {\n"
                + "    \"text\" : \"Factura Completa\",\n"
                + "    \"type\" : \"SIMPLIFIED\",\n"
                + "    \"items\" : [ {\n"
                + "      \"quantity\" : \"1\",\n"
                + "      \"system\" : {\n"
                + "        \"type\" : \"REGULAR\",\n"
                + "        \"category\" : {\n"
                + "          \"type\" : \"VAT\",\n"
                + "          \"rate\" : \"21\"\n"
                + "        }\n"
                + "      },\n"
                + "      \"discount\" : \"10\",\n"
                + "      \"text\" : \"Curso B\",\n"
                + "      \"unit_amount\" : \"250\",\n"
                + "      \"vat_type\" : \"IVA\",\n"
                + "      \"full_amount\" : \"290.40\"\n"
                + "    } ],\n"
                + "    \"number\" : \"1\",\n"
                + "    \"full_amount\" : \"290.40\"\n"
                + "  },\n"
                + "  \"recipients\" : [ {\n"
                + "    \"id\" : {\n"
                + "      \"registered\" : true,\n"
                + "      \"tax_number\" : \"B22260863\",\n"
                + "      \"legal_name\" : \"ARAGON FORMACION ACF S.L.\"\n"
                + "    },\n"
                + "    \"address_line\" : \"C/ Comercio 28\",\n"
                + "    \"postal_code\" : \"22006\"\n"
                + "  } ],\n"
                + "  \"type\" : \"COMPLETE\"\n"
                + "} }";

        // Extraer solo el objeto interno
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String jsonParaDTO = root.get("content").toString();

        // Deserializar al DTO
        ContentCompleteDTO content = JsonUtil.fromJson(jsonParaDTO, ContentCompleteDTO.class);

        // Verificar campos principales
        assertNotNull(content, "Falta el campo 'content'");
        assertNotNull(content.getData(), "Falta el campo 'data'");
        assertNotNull(content.getRecipients(), "Falta el campo 'recipients'");
        assertNotNull(content.getType(), "Falta el campo 'type'");

        // Verificar datos de 'data'
        DataDTO data = content.getData();
        assertNotNull(data.getText(), "Falta 'data.text'");
        assertNotNull(data.getType(), "Falta 'data.type'");
        assertNotNull(data.getItems(), "Falta 'data.items'");
        assertNotNull(data.getInvoiceNumber(), "Falta 'data.number'");
        assertNotNull(data.getFullAmount(), "Falta 'data.full_amount'");

        // Verificar primer item
        ItemDTO item = data.getItems().get(0);
        assertNotNull(item.getQuantity(), "Falta 'item.quantity'");
        assertNotNull(item.getSystem(), "Falta 'item.system'");
        assertNotNull(item.getText(), "Falta 'item.text'");
        assertNotNull(item.getUnitAmount(), "Falta 'item.unit_amount'");
        assertNotNull(item.getVatype(), "Falta 'item.vat_type'");
        assertNotNull(item.getFullAmount(), "Falta 'item.full_amount'");

        // Verificar system.category
        assertNotNull(item.getSystem().getCategory(), "Falta 'item.system.category'");
        assertNotNull(item.getSystem().getCategory().getType(), "Falta 'category.type'");
        assertNotNull(item.getSystem().getCategory().getRate(), "Falta 'category.rate'");

        // Verificar primer recipient
        RecipientsDTO recipient = content.getRecipients().get(0);
        assertNotNull(recipient.getId(), "Falta 'recipient.id'");
        assertNotNull(recipient.getId().getTaxNumber(), "Falta 'recipient.id.tax_number'");
        assertNotNull(recipient.getId().getLegalName(), "Falta 'recipient.id.legal_name'");
        assertNotNull(recipient.getAddressLine(), "Falta 'recipient.address_line'");
        assertNotNull(recipient.getPostalCode(), "Falta 'recipient.postal_code'");
    }
}

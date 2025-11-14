/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.pruebaFiskaly;

import org.json.JSONObject;
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
public class TaxpayersTest {

    public TaxpayersTest() {
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
     * Test of retrieveTaxpayer method, of class Taxpayers.
     */
    @Test
    public void testRetrieveTaxpayer() {
        System.out.println("retrieveTaxpayer");
        JSONObject json = Taxpayers.retrieveTaxpayer();
        assertNotNull(json, "La respuesta no debe ser nula");

        JSONObject content = json.getJSONObject("content");
        JSONObject issuer = content.getJSONObject("issuer");

        assertEquals("ARAGON FORMACION ACF S.L.", issuer.getString("legal_name"));
        assertEquals("B22260863", issuer.getString("tax_number"));
        assertEquals("ENABLED", content.getString("state"));
        assertEquals("SPAIN_OTHER", content.getString("territory"));
        assertEquals("COMPANY", content.getString("type"));
    }
}

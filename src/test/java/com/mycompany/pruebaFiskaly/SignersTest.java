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
public class SignersTest {

    public SignersTest() {
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
     * Test of listSigners method, of class Signers.
     */
    @Test
    public void testListSigners() {
        String response = Signers.listSigners();
        assertNotNull(response, "La respuesta de listSigners no debe ser nula");
        JSONObject json = new JSONObject(response);
        assertTrue(json.has("results"), "La respuesta debe contener 'results'");
        assertTrue(json.getJSONArray("results").length() > 0, "Debe haber al menos un signer");
    }

    /**
     * Test of getFirstSignerID method, of class Signers.
     */
    @Test
    public void testGetFirstSignerID() {
        System.out.println("getFirstSignerID");
        String expResult = "117e61c5-035d-4479-9f54-cf96ed088f06"; //Primer Signer en DASHBOARD
        String result = Signers.getFirstSignerID();
        assertNotNull(result, "El ID del primer signer no debe ser nulo");
        assertFalse(result.isEmpty(), "El ID del signer no debe estar vacío");
        assertEquals(expResult, result);

    }

}

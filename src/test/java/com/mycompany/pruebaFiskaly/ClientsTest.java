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
public class ClientsTest {

    public ClientsTest() {
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
     * Test of listClients method, of class Clients.
     */
    @Test
    public void testListClients() {
        System.out.println("listClients");
        String response = Clients.listClients();
        assertNotNull(response, "La respuesta no debe ser nula");

        try {
            JSONObject json = new JSONObject(response);
            assertTrue(json.has("results"), "La respuesta debe contener 'results'");
        } catch (Exception e) {
            fail("La respuesta no es un JSON válido: " + e.getMessage());
        }
    }

    /**
     * Test of getFirstClientID method, of class Clients.
     */
    @Test
    public void testGetFirstClientID() {
        System.out.println("getFirstClientID");
        String clientId = Clients.getFirstClientID();
        assertEquals("141d226f-0b03-4831-992f-b4c8b9626dc2", Clients.getFirstClientID()); //Primer Client en DASHBOARD
        assertNotNull(clientId, "El ID del primer client no debe ser nulo");
        assertFalse(clientId.isEmpty(), "El ID del client no debe estar vacío");
        System.out.println("Primer client ID: " + clientId);
    }
}

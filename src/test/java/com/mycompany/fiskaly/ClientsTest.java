/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.fiskaly;

import com.mycompany.fiskaly.Clients;
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
        //assertEquals("2033e6e6-cf46-4523-ac14-2456e19c77c4", Clients.getFirstClientID()); //Primer Client en DASHBOARD
        assertNotNull(clientId, "El ID del primer client no debe ser nulo");
        assertFalse(clientId.isEmpty(), "El ID del client no debe estar vacío");
        System.out.println("Primer client ID: " + clientId);
    }
}

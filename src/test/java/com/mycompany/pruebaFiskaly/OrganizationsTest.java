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
public class OrganizationsTest {

    public OrganizationsTest() {
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
     * Test of listOrganizations method, of class Organizations.
     */
    @Test
    public void testListOrganizations() {
        System.out.println("listOrganizations");
        String response = Organizations.listOrganizations();
        assertNotNull(response, "La respuesta de listOrganizations no debe ser nula");

        JSONObject json = new JSONObject(response);
        assertTrue(json.has("data"), "El JSON debe contener el campo 'data'");
        assertTrue(json.getJSONArray("data").length() > 0, "Debe haber al menos una organización");
    }

    /**
     * Test of GetFirstOrganizationID method, of class Organizations.
     */
    @Test
    public void testGetFirstOrganizationID() {
        System.out.println("getFirstOrganizationID");
        String organizationID = "ec1e055b-f2dd-43b7-b550-4624575b0674"; // ID de la organización
        assertNotNull(organizationID, "El ID de la primera organización no debe ser nulo");
        assertFalse(organizationID.isEmpty(), "El ID de la organización no debe estar vacío");
        System.out.println("Primer organization ID: " + organizationID);
    }
}

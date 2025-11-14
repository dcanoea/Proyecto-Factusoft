/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.pruebaFiskaly;

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
public class AuthenticationTest {

    public AuthenticationTest() {
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
     * Test of retrieveToken method, of class Authentication.
     */
    @Test
    public void testRetrieveToken() throws Exception {
        System.out.println("retrieveToken");
        String token = Authentication.retrieveToken();
        assertNotNull(token, "El token no debe ser nulo");
        assertFalse(token.isEmpty(), "El token no debe estar vacío");
        System.out.println("Token recibido: " + token);
    }

}

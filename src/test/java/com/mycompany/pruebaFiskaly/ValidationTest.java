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
public class ValidationTest {

    public ValidationTest() {
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
     * Test of validateTaxID method, of class Validation.
     */
    @Test
    public void testValidateTaxID() {
        System.out.println("validateTaxID");
        String nif = "18053094A";
        boolean result = Validation.validateTaxID(nif);
        assertTrue(result);
        assertFalse(Validation.validateTaxID("123456789A"));
        assertTrue(Validation.validateTaxID("B22114532"));
        assertFalse(Validation.validateTaxID("B1234567890"));
        assertTrue(Validation.validateTaxID("X1234567L"));
        assertFalse(Validation.validateTaxID("Z1234567X"));
        assertFalse(Validation.validateTaxID(null));
        assertFalse(Validation.validateTaxID(""));
    }

    /**
     * Test of validateAEAT method, of class Validation.
     */
    @Test
    public void testValidateAEAT() {
        System.out.println("validateAEAT");
        String nif = "18053094A";
        String expResult = "INVALID";
        String result = Validation.validateAEAT(nif);
        assertEquals(expResult, result);
        assertEquals("INVALID_SIMILAR", Validation.validateAEAT("T00000001"));
        assertEquals("VALID", Validation.validateAEAT("T00000002"));
        assertEquals("VALID_REVOKED", Validation.validateAEAT("T00000003"));
        assertEquals("VALID_REMOVED", Validation.validateAEAT("T00000004"));
        assertEquals(false, Validation.validateTaxID(null));
        assertEquals(false, Validation.validateTaxID(""));
    }

    /**
     * Test of validateVIES method, of class Validation.
     */
    @Test
    public void testValidateVIES() {
        System.out.println("validateVIES");
        assertFalse(Validation.validateVIES("ES", "18053554B"));
        assertEquals(true, Validation.validateVIES("ES", "B44752210"));
        assertEquals(false, Validation.validateVIES("ES", "B4433333210"));
        assertEquals(false, Validation.validateVIES("", ""));
        assertEquals(false, Validation.validateVIES(null, null));
    }
}

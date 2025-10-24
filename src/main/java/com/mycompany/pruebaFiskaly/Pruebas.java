package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Invoices.Complete;
import com.mycompany.pruebaFiskaly.Invoices.Correcting;
import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;
import com.mycompany.pruebaFiskaly.Invoices.Simplified;
import com.mycompany.pruebaFiskaly.Invoices.Summary;
import com.mycompany.pruebaFiskaly.Invoices.SummaryCOMPLETES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pruebas {

    public static int NUM_FACTURA = 20250065;

    public static void main(String[] args) throws IOException {

//-------------------------------------------------------------------------------------------------------------------------------
        //Validation.Java
        //VALIDACIÓN NIF AEAT
        //System.out.println(Validation.validateAEAT("T00000001"));
        //System.out.println(Validation.validateAEAT("T00000002"));
        //System.out.println(Validation.validateAEAT("T00000003"));
        //System.out.println(Validation.validateAEAT("T00000004"));
        //VALIDACIÓN NIF VIES
        //System.out.println(Validation.validateVIES("ES", "B44752210"));
        //System.out.println(Validation.validateVIES("ES", "B4433333210"));   
//-------------------------------------------------------------------------------------------------------------------------------
        //INVOICES
        // LISTAR FACTURAS 
        //InvoicesManagement.listInvoices();
        // BUSCAR ID DE FACTURA POR Nº
        //String invoiceid = InvoicesManagement.getInvoiceIDByNumber("RC-2025-001");
        //System.out.println(invoiceid);
        //BUSCAR Nº DE FACTURA POR ID
        //String invoiceNumber = InvoicesManagement.getInvoiceNumberByID("b6134f79-cb3c-4254-ae1d-c4a720c62a42");
        //System.out.println(invoiceNumber);
        // OBTENER DETALLES DE UNA FACTURA
        String invoiceid = InvoicesManagement.getInvoiceIDByNumber("20250065");
        InvoicesManagement.retrieveInvoice(invoiceid);
        // OBTENER ESTADO DE ERROR DE LA FACTURA Y SU DESCRIPCION
        //String invoiceNumber = InvoicesManagement.getInvoiceNumberByID("b6134f79-cb3c-4254-ae1d-c4a720c62a42");
        //InvoicesManagement.getRegistrationDescription(invoiceNumber, InvoicesManagement.getInvoiceIDByNumber(invoiceNumber));
        // CREAR FACTURA SIMPLIFICADA
        /*List<Map<String, String>> lineasFactura = new ArrayList<>();
        Simplified.createItem(lineasFactura, "Prueba", "1", "123", "21");
        Simplified.createItem(lineasFactura, "Prueba2", "2", "254", "4");
        Simplified.createItem(lineasFactura, "Prueba3", "3", "25", "10");
        Simplified.createSimplifiedInvoice(NUM_FACTURA, lineasFactura);*/
        // CREAR 10 FACTURAS SIMPLIFICADAS PARA PRUEBAS RECAPITULATIVAS
        /*for (int i = 0; i < 10; i++) {
            Simplified.createSimplifiedInvoice(NUM_FACTURA, lineasFactura);
            NUM_FACTURA++;
        }*/
        // CREAR FACTURA COMPLETA
        /*List<Map<String, String>> itemsList = new ArrayList<>();
        Complete.createItem(itemsList, "Prueba1", "1", "111", "21");
        Complete.createItem(itemsList, "Prueba2", "2", "222", "4");
        Complete.createItem(itemsList, "Pureba3", "3", "333", "10");
        
        Map<String,String> receptorDetails = Complete.createReceptor("David Cano Escario", "18053094A", true, "Paseo Ramon y Cajal", "22006");

        Complete.createCompleteInvoice(NUM_FACTURA, itemsList, receptorDetails);*/
        // CREAR FACTURAS RECTIFICATIVAS
        //String numeroFactura = "F-2025-008R";
        //String idFactura = InvoicesManagement.getInvoiceId(numeroFactura);
        /*List<Map<String, String>> itemsList = new ArrayList<>();
        Complete.createItem(itemsList, "Hora simulador", "5", "50", "21");
        Complete.createItem(itemsList, "Curso de carretillero", "1", "357", "4");
        Complete.createItem(itemsList, "Horas practicas", "3", "159", "10");
        
        Map<String,String> receptorDetails = Complete.createReceptor("David Cano Escario", "18053094A", true, "Paseo Ramon y Cajal", "22006");

        Correcting.createCorrectingInvoiceSubstitutionComplete(20250064, NUM_FACTURA, itemsList, receptorDetails);*/
        //Correcting.createCorrectingInvoiceSubstitutionSimplified(numeroFactura, idFactura);
        //Correcting.createCorrectingDifferencesSimplified(numeroFactura, idFactura);
        //Correcting.createCorrectingInvoiceDifferencesComplete(numeroFactura, idFactura); 
        // RECUPERAR TOTAL FACTURA 
        //String idFactura = InvoicesManagement.getInvoiceIDByNumber("S-2025-009"); //7891d62c-7eba-40e2-a058-405d8a2b4718
        //System.out.println(InvoicesManagement.getFullAmount(idFactura));
        // FACTURA RECAPITULATIVA
        /*List<String> numerosFacturas = new ArrayList<>();
        int numFactura = 20250051;
        for (int i = 0; i < 10; i++) {
            numerosFacturas.add(String.valueOf(numFactura));
            numFactura++;
        }
        Summary.createSummaryInvoice(NUM_FACTURA, numerosFacturas);*/
        //SummaryCOMPLETES.createSummaryCompleteInvoice(NUM_FACTURA, numerosFacturas);
        
        //MÉTODO CANCELAR FACTURA (FUNCIONA, PERO NO VEO CUANDO SE PUEDE USAR)
        //InvoicesManagement.cancelInvoice("20250063");
       
    }
}

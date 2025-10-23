package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Invoices.Complete;
import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;
import com.mycompany.pruebaFiskaly.Invoices.Simplified;
import com.mycompany.pruebaFiskaly.Invoices.Summary;
import com.mycompany.pruebaFiskaly.Invoices.SummaryCOMPLETES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pruebas {

    public static int NUM_FACTURA = 20250060;

    public static void main(String[] args) throws IOException {
        //Authentication.java
        // RECUPERAR TOKEN
        //System.out.println(Authentication.retrieveToken().toString());
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
        //Taxpayers.java
        //CREAR CONTRIBUYENTE
        //Taxpayers.createTaxpayer("ARAGON FORMACION ACF S.L.", "B22260863", "SPAIN_OTHER");
        // RECUPERAR CONTRIBUYENTE
        //Taxpayers.retrieveTaxpayer();
        //ACTUALIZAR CONTRIBUYENTE
        /*Taxpayers.updateTaxpayer(
                false,
                "ARAGON FORMACION ACF S.L.",
                "B22260863",
                "Huesca",
                "Huesca",
                "C/ Comercio",
                "22006",
                "28",
                "ES",
                "itformacion@acfinnove.com",
                "company",
                "+34 625220697",
                "https://acfinnove.com/",
                "Fernando",
                null,
                null,
                "2025",
                "Education",
                "es",
                "Europe/Madrid",
                "Actualización de datos fiscales"
        );*/
//-------------------------------------------------------------------------------------------------------------------------------
        //Organizations.java
        //LISTAR ORGANIZACIONES
        //Organizations.listOrganizations();
        //RECUPERAR ORGANIZACION
        //Organizations.retrieveOrganization("ec1e055b-f2dd-43b7-b550-4624575b0674");
//-------------------------------------------------------------------------------------------------------------------------------
        //Signers.java
        // CREAR FIRMANTE
        //Signers.createSigner();
        // LISTAR FIRMANTES
        // Signers.listSigners();
        // RECUPERAR ID DEL PRIMER FIRMANTE
        //String signerId = Signers.getFirstSigner_Id();
        //System.out.println(signerId);
//-------------------------------------------------------------------------------------------------------------------------------
        //Clients.java
        // CREAR CLIENTE
        //Clients.createClient();
        // LISTAR CLIENTES
        //Clients.listClients();
        // RECUPERAR ID EL PRIMER CLIENTE
        //String clientId = Clients.getFirstClientId();
        //System.out.println(clientId);
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
        //String invoiceid = InvoicesManagement.getInvoiceIDByNumber("20250059");
        //InvoicesManagement.retrieveInvoice(invoiceid);
        // OBTENER ESTADO DE ERROR DE LA FACTURA Y SU DESCRIPCION
        //String invoiceNumber = InvoicesManagement.getInvoiceNumberByID("b6134f79-cb3c-4254-ae1d-c4a720c62a42");
        //InvoicesManagement.getRegistrationDescription(invoiceNumber, InvoicesManagement.getInvoiceIDByNumber(invoiceNumber));
        // CREAR FACTURA SIMPLIFICADA
        //Simplified.createSimplifiedInvoice(NUM_FACTURA);
        // CREAR 10 FACTURAS SIMPLIFICADAS PARA PRUEBAS RECAPITULATIVAS
        /*for (int i = 0; i < 10; i++) {
            Simplified.createSimplifiedInvoice(NUM_FACTURA);
            NUM_FACTURA++;
        }*/
        // CREAR FACTURA COMPLETA
        //Complete.createCompleteInvoice(NUM_FACTURA);
        // CREAR FACTURAS RECTIFICATIVAS
        //String numeroFactura = "F-2025-008R";
        //String idFactura = InvoicesManagement.getInvoiceId(numeroFactura);
        //Correcting.createCorrectingInvoiceSubstitutionComplete(numeroFactura,idFactura);
        //Correcting.createCorrectingInvoiceSubstitutionSimplified(numeroFactura, idFactura);
        //Correcting.createCorrectingDifferencesSimplified(numeroFactura, idFactura);
        //Correcting.createCorrectingInvoiceDifferencesComplete(numeroFactura, idFactura); 
        // RECUPERAR TOTAL FACTURA 
        //String idFactura = InvoicesManagement.getInvoiceIDByNumber("S-2025-009"); //7891d62c-7eba-40e2-a058-405d8a2b4718
        //System.out.println(InvoicesManagement.getFullAmount(idFactura));
        // FACTURA RECAPITULATIVA
        List<String> numerosFacturas = new ArrayList<>();
        int numFactura = 20250051;
        for (int i = 0; i < 10; i++) {
            numerosFacturas.add(String.valueOf(numFactura));
            numFactura++;
        }

        SummaryCOMPLETES.createSummaryCompleteInvoice(NUM_FACTURA, numerosFacturas);

    }
}

package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;
import com.mycompany.pruebaFiskaly.Invoices.Simplified;
import com.mycompany.pruebaFiskaly.Invoices.Summary;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pruebas {

    public static void main(String[] args) throws IOException {
        // RECUPERAR TOKEN
        //System.out.println(Authentication.retrieveToken().toString());

        //VALIDACIÓN NIF AEAT
        //System.out.println(Validation.validateAEAT("T00000001"));
        //System.out.println(Validation.validateAEAT("T00000002"));
        //System.out.println(Validation.validateAEAT("T00000003"));
        //System.out.println(Validation.validateAEAT("T00000004"));
        
        //VALIDACIÓN NIF VIES
        //System.out.println(Validation.validateVIES("ES", "B44752210"));
        //System.out.println(Validation.validateVIES("ES", "B4433333210"));   
        
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

        //LISTAR ORGANIZACIONES
        //Organizations.listOrganizations();
        
        //RECUPERAR ORGANIZACION
        //Organizations.retrieveOrganization("ec1e055b-f2dd-43b7-b550-4624575b0674");
        
        // CREAR FIRMANTE
        //Signers.createSigner();
        
        // LISTAR FIRMANTES
        // Signers.listSigners();
        
        // RECUPERAR ID DEL PRIMER FIRMANTE
        //String signerId = Signers.getFirstSigner_Id();
        //System.out.println(signerId);
                      
        // CREAR CLIENTE
        //Clients.createClient();
        
        // LISTAR CLIENTES
        //Clients.listClients();
        
        // RECUPERAR ID EL PRIMER CLIENTE
        //String clientId = Clients.getFirstClientId();
        //System.out.println(clientId);
        
        // CREAR FACTURA SIMPLIFICADA
        //Simplified.createSimplifiedInvoice();
        
        // CREAR FACTURA COMPLETA
        //Complete.createCompleteInvoice();
        
        // LISTAR FACTURAS 
        //InvoicesManagement.listInvoices();
        
        // BUSCAR FACTURA POR Nº
        //String invoiceid = InvoicesManagement.getInvoiceID("C-2025-004"); // id d7ee6242-282c-4e87-94b8-dcc4be9d6466
        //System.out.println(invoice_id);
        
        // OBTENER DETALLES DE UNA FACTURA
        //String invoice_id = InvoicesManagement.getInvoiceID("RC-2025-001"); 
        //InvoicesManagement.retrieveInvoice(invoice_id);
        
        // CREAR FACTURAS RECTIFICATIVAS
        //String numeroFactura = "F-2025-008R";
        //String idFactura = InvoicesManagement.getInvoiceId(numeroFactura);
        //Correcting.createCorrectingInvoiceSubstitutionComplete(numeroFactura,idFactura);
        //Correcting.createCorrectingInvoiceSubstitutionSimplified(numeroFactura, idFactura);
        //Correcting.createCorrectingDifferencesSimplified(numeroFactura, idFactura);
        //Correcting.createCorrectingInvoiceDifferencesComplete(numeroFactura, idFactura);       
        
        // RECUPERAMOS ID_FACTURA Y TOTAL FACTURA 
        //String idFactura = InvoicesManagement.getInvoiceId("S-2025-009"); //7891d62c-7eba-40e2-a058-405d8a2b4718
        //System.out.println("idFactura -> " + idFactura + " --- Total -> " + InvoicesManagement.getFullAmount(idFactura));
        
                /*
        // FACTURA RECAPITULATIVA
        List<String> numerosFacturas = new ArrayList<>();
        numerosFacturas.add("S-2025-015");
        numerosFacturas.add("S-2025-016");
        numerosFacturas.add("S-2025-017");
        
        Summary.createSummaryInvoice(numerosFacturas);
        */
        
        
        //MÉTODO PARA RECUPERAR EL ESTADO DE ERROR DE LA FACTURA
        
        
        //MÉTODO PARA CORREGIR LA FACTURA
        
        
        
        


    }
}

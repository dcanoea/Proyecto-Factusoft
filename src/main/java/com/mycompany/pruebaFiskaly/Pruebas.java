package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Invoices.Complete;
import com.mycompany.pruebaFiskaly.Invoices.Correcting;
import com.mycompany.pruebaFiskaly.Invoices.Invoices_Management;
import com.mycompany.pruebaFiskaly.Invoices.Simplified;
import java.io.IOException;

public class Pruebas {

    public static void main(String[] args) throws IOException {
        // RECUPERAR TOKEN
        // System.out.println(Authentication.retrieve_token());

        //VALIDACIÓN NIF AEAT
        //System.out.println(Validation.validate_AEAT("T00000001"));
        //System.out.println(Validation.validate_AEAT("T00000002"));
        //System.out.println(Validation.validate_AEAT("T00000003"));
        //System.out.println(Validation.validate_AEAT("T00000004"));
        
        //VALIDACIÓN NIF VIES
        //System.out.println(Validation.validate_VIES("ES", "B44752210"));
        //System.out.println(Validation.validate_VIES("ES", "B4433333210"));   
        
        //CREAR CONTRIBUYENTE
        //Taxpayer.create_Taxpayer("ARAGON FORMACION ACF S.L.", "B22260863", "SPAIN_OTHER");
        
        // RECUPERAR CONTRIBUYENTE
        //Taxpayer.retrieve_Taxpayer();

        //ACTUALIZAR CONTRIBUYENTE
        /*Taxpayer.update_Taxpayer(
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
        //Organizations.list_Organizations();
        
        //RECUPERAR ORGANIZACION
        //Organizations.retrieve_Organization("ec1e055b-f2dd-43b7-b550-4624575b0674");
        
        // CREAR FIRMANTE
        //Signers.create_Signer();
        
        // LISTAR FIRMANTES
        // Signers.list_Signers();
        
        // RECUPERAR ID DEL PRIMER FIRMANTE
        //String signerId = Signers.get_First_Signer_Id();
        //System.out.println(signerId);
                      
        // CREAR CLIENTE
        //Clients.create_Client();
        
        // LISTAR CLIENTES
        //Clients.list_Clients();
        
        // RECUPERAR ID EL PRIMER CLIENTE
        //String clientId = Clients.get_First_Client_Id();
        //System.out.println(clientId);
        
        // CREAR FACTURA SIMPLIFICADA
        //Simplified.create_Simplified_Invoice();
        
        // CREAR FACTURA COMPLETA
        //Complete.create_Complete_Invoice();
        
        // LISTAR FACTURAS 
        //String client_id = Clients.get_First_Client_Id();
        //Invoices_Management.list_Invoices(client_id);
        
        // BUSCAR FACTURA POR Nº
        //String invoice_id = Invoices_Management.get_Invoice_Id("C-2025-004"); // id d7ee6242-282c-4e87-94b8-dcc4be9d6466}
        //System.out.println(invoice_id);
        
        //A PARTIR DE AQUI NO ESTÁN EN FUNCIONAMIENTO
        
        // CREAR FACTURAS RECTIFICATIVAS
        String numeroFactura = "F-2025-008R";
        String idFactura = Invoices_Management.get_Invoice_Id(numeroFactura);
        //Correcting.createCorrectingInvoice_Substitution_Complete( numeroFactura,idFactura);
        //Correcting.createCorrectingInvoice_Substitution_Simplified(numeroFactura, idFactura);
        //Correcting.create_Correcting_Difference_Simplified(numeroFactura, idFactura);
        
        Correcting.createCorrectingInvoice_Differences_Complete(numeroFactura, idFactura);
        
    }
}

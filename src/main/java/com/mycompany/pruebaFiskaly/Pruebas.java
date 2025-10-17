package com.mycompany.pruebaFiskaly;

import java.io.IOException;

public class Pruebas {

    public static void main(String[] args) throws IOException {
        // RECUPERAR TOKEN
        //System.out.println(Authentication.retrieve_token());

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

                
        //A PARTIR DE AQUI NO ESTÁN EN FUNCIONAMIENTO
        
        //CREAR CLIENTE
        Clients.create_client();
        
        //LISTAR CLIENTES
        //Clients.list_clients();
        
        //LISTAR ORGANIZACIONES
        //Organizations.list_Organizations();
        
        //RECUPERAR ORGANIZACION
        //Organizations.retrieve_Organization("ec1e055b-f2dd-43b7-b550-4624575b0674");
    }
}

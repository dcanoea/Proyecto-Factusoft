package com.mycompany.pruebaFiskaly;

import java.io.IOException;

public class Pruebas {

    public static void main(String[] args) throws IOException {
        // RECUPERAR TOKEN
        System.out.println(Authentication.retrieve_token());
        
        //VALIDACIÓN NIF AEAT
        System.out.println(Validation.validate_AEAT("T00000001"));
        System.out.println(Validation.validate_AEAT("T00000002"));
        System.out.println(Validation.validate_AEAT("T00000003"));
        System.out.println(Validation.validate_AEAT("T00000004"));
         
        //VALIDACIÓN NIF VIES
        System.out.println(Validation.validate_VIES("ES", "B44752210"));
        System.out.println(Validation.validate_VIES("ES", "B4433333210"));   
        
        //CREAR CLIENTE
        Clients.create_client();
        
        //LISTAR CLIENTES
        Clients.list_clients();
        
        //LISTAR ORGANIZACIONES
        Organizations.list_Organizations();
        
        //RECUPERAR ORGANIZACION
        Organizations.retrieve_Organization("41848007-4f74-4da8-856b-3caaf9514e57");
    }
}

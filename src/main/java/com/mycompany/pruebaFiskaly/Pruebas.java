package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Authentication.Retrieve_token;
import com.mycompany.pruebaFiskaly.Validation.Validate_NIF;
import java.io.IOException;

public class Pruebas {

    public static void main(String[] args) throws IOException {
        // RECUPERAR TOKEN
        System.out.println(Retrieve_token.get_token());
        
        //VALIDACIÓN NIF AEAT
        System.out.println(Validate_NIF.validate_AEAT("T00000001"));
        System.out.println(Validate_NIF.validate_AEAT("T00000002"));
        System.out.println(Validate_NIF.validate_AEAT("T00000003"));
        System.out.println(Validate_NIF.validate_AEAT("T00000004"));
         
        //VALIDACIÓN NIF VIES
        System.out.println(Validate_NIF.validate_VIES("ES", "B44752210"));
        System.out.println(Validate_NIF.validate_VIES("ES", "B4433333210"));     
    }
}

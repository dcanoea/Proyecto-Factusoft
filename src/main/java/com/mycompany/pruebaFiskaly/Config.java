package com.mycompany.pruebaFiskaly;

import java.util.UUID;

public class Config {

    // URL de Fiskaly SIGN ES
    // public static final String BASE_URL = "https://live.es.sign.fiskaly.com/api/v1"; // modo LIVE API SIGN ES
    public static final String BASE_URL = "https://test.es.sign.fiskaly.com/api/v1"; // Modo Test API SIGN ES

    public static final String BASE_MANAGEMENT_URL = "https://dashboard.fiskaly.com/api/v0"; // API Management

    // Credenciales
    public static final String API_KEY = "test_dz8evcra8g5lccbn2xt6a2kj8_acfmanaged";
    public static final String API_SECRET = "NhWMs0VpVe228dqr4CJUY8LKjyDofTqj08vCoxRzaWT";

    public static String clientID = "ce46883c-5425-4a85-bf15-f04141ba13e5";	
;
    public static UUID random_UUID = UUID.randomUUID();

    // Endpoints API
    public static final String AUTH = "/auth";
    public static final String INVOICES = "/clients/" + clientID + "/invoices";
    public static final String CREATE_INVOICE = "/clients/" + clientID + "/invoices/" + random_UUID;
    public static final String CLIENTS = "/clients";
    public static final String CREATE_CLIENT = "/clients/" + random_UUID;
    public static final String ORGANIZATIONS = "/organizations";
    public static final String SIGNERS = "/signers";
    public static final String CREATE_SIGNER = "/signers/" + random_UUID;
    public static final String TAXPAYER = "/taxpayer";
    public static final String VALIDATION_TIN = "/validation/tin";

}

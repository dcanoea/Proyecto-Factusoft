package com.mycompany.fiskaly;

import java.util.UUID;

public class Config {

    // URL de Fiskaly SIGN ES
    // public static final String BASE_URL = "https://live.es.sign.fiskaly.com/api/v1"; // modo LIVE API SIGN ES
    public static final String BASE_URL = "https://test.es.sign.fiskaly.com/api/v1"; // Modo Test API SIGN ES

    public static final String BASE_MANAGEMENT_URL = "https://dashboard.fiskaly.com/api/v0"; // API Management

    // Credenciales
    public static final String API_KEY = "test_5e1qffwp4kcg6x6a8kni60p36_prueba";
    public static final String API_SECRET = "P6IsEgkapDZjaoWQLKARPr1kd1zyp64If1dNfcUWM8n";

    public static String clientID = Clients.getFirstClientID();	

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

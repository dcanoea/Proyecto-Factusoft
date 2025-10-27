package com.mycompany.pruebaFiskaly;

public class Config {

    // URL de Fiskaly SIGN ES (en modo test)
    // public static final String BASE_URL = "https://live.es.sign.fiskaly.com/api/v1"; // modo LIVE API SIGN ES
    public static final String BASE_URL = "https://test.es.sign.fiskaly.com/api/v1"; // Modo Test API SIGN ES

    public static final String BASE_MANAGEMENT_URL = "https://dashboard.fiskaly.com/api/v0"; // API Management

    // Credenciales
    public static final String API_KEY = "test_dz8evcra8g5lccbn2xt6a2kj8_acfmanaged";
    public static final String API_SECRET = "NhWMs0VpVe228dqr4CJUY8LKjyDofTqj08vCoxRzaWT";

    public static final String IVA_GENERAL = "21";
    public static final String IVA_REDUCIDO = "10";
    public static final String IVA_SUPERREDUCIDO = "4";
    public static final String IVA_EXENTO = "exento";
    public static final String IVA_SUPLIDO = "suplido";
    public static final String IVA_PRORRATA = "37";
}

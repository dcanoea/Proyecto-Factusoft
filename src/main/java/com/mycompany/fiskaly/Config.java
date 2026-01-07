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
    public static String AUTH = "/auth";
    public static String INVOICES = "/clients/" + clientID + "/invoices";
    public static String CREATE_INVOICE = "/clients/" + clientID + "/invoices/" + random_UUID;
    public static String CLIENTS = "/clients";
    public static String CREATE_CLIENT = "/clients/" + random_UUID;
    public static String ORGANIZATIONS = "/organizations";
    public static String SIGNERS = "/signers";
    public static String CREATE_SIGNER = "/signers/" + random_UUID;
    public static String TAXPAYER = "/taxpayer";
    public static String VALIDATION_TIN = "/validation/tin";

    public static void refrescarUUID() {
        // 1. AUTO-CORRECCIÓN: Si el clientID es null (porque falló al arrancar), lo arreglamos ahora
        if (clientID == null || clientID.equals("null")) {
            System.out.println("Detectado ClientID nulo. Intentando recuperar...");
            try {
                // Opción A: Intentar pedirlo a la API otra vez (ahora que ya hay conexión)
                clientID = Clients.getFirstClientID();
            } catch (Exception e) {
                System.err.println("Falló la recuperación automática.");
            }
        }

        // 2. Generamos nuevo UUID para la transacción
        random_UUID = UUID.randomUUID();

        // 3. Reconstruimos las URLs (Ahora clientID ya tiene valor seguro)
        CREATE_INVOICE = "/clients/" + clientID + "/invoices/" + random_UUID;

        // Actualizamos el resto por si acaso
        CREATE_CLIENT = "/clients/" + random_UUID;
        CREATE_SIGNER = "/signers/" + random_UUID;

        System.out.println(">>> Config refrescada. ClientID: " + clientID + " | Nuevo UUID Tx: " + random_UUID);
    }
}
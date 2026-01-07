package com.mycompany.fiskaly;

import java.util.UUID;

public class Config {

    public static final String BASE_URL = "https://test.es.sign.fiskaly.com/api/v1";
    public static final String BASE_MANAGEMENT_URL = "https://dashboard.fiskaly.com/api/v0";

    public static final String API_KEY = "test_5e1qffwp4kcg6x6a8kni60p36_prueba";
    public static final String API_SECRET = "P6IsEgkapDZjaoWQLKARPr1kd1zyp64If1dNfcUWM8n";

    // Inicializamos como null para que no dispare la API al arrancar
    public static String clientID = null;
    public static UUID random_UUID = UUID.randomUUID();

    // Endpoints base (estáticos)
    public static final String AUTH = "/auth";
    public static final String CLIENTS = "/clients";
    public static final String ORGANIZATIONS = "/organizations";
    public static final String SIGNERS = "/signers";
    public static final String TAXPAYER = "/taxpayer";
    public static final String VALIDATION_TIN = "/validation/tin";

    // Estas variables ahora serán dinámicas (se actualizarán con refrescarUUID)
    public static String INVOICES = "";
    public static String CREATE_INVOICE = "";
    public static String CREATE_CLIENT = "";
    public static String CREATE_SIGNER = "";

    /**
     * Este método debe llamarse UNA VEZ al iniciar el programa y cada vez que
     * se necesite una nueva transacción.
     */
    public static void refrescarUUID() {
        // 1. Obtener el ClientID de forma segura si no existe
        if (clientID == null || clientID.isEmpty() || clientID.equals("null")) {
            System.out.println(">>> Config: Recuperando ClientID por primera vez...");
            // Llamamos al método directamente. Al no ser una inicialización estática, no hay bloqueo.
            clientID = Clients.getFirstClientID();
        }

        // 2. Generar nuevo UUID para la operación
        random_UUID = UUID.randomUUID();

        // 3. Reconstruir URLs dinámicas solo si tenemos el ID
        if (clientID != null) {
            INVOICES = "/clients/" + clientID + "/invoices";
            CREATE_INVOICE = "/clients/" + clientID + "/invoices/" + random_UUID;
        }

        CREATE_CLIENT = "/clients/" + random_UUID;
        CREATE_SIGNER = "/signers/" + random_UUID;

        System.out.println(">>> Config refrescada con éxito. ClientID: " + clientID);
    }
}

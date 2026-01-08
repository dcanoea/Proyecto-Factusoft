package com.mycompany.fiskaly;

import java.util.UUID;
import java.util.prefs.Preferences; // <--- NECESARIO PARA GUARDAR CONFIGURACIÓN

public class Config {

    // --- SECCIÓN DE PERSISTENCIA (GUARDAR DATOS) ---
    // 1. Creamos el objeto que gestiona la memoria persistente
    private static final Preferences prefs = Preferences.userNodeForPackage(Config.class);

    // 2. Nombres internos para identificar los datos en el registro
    private static final String KEY_API_KEY = "fiskaly_api_key";
    private static final String KEY_API_SECRET = "fiskaly_api_secret";
    // -----------------------------------------------

    public static final String BASE_URL = "https://test.es.sign.fiskaly.com/api/v1";
    public static final String BASE_MANAGEMENT_URL = "https://dashboard.fiskaly.com/api/v0";

    // 3. AL ARRANCAR: Intentamos leer de la memoria. 
    // Si no hay nada guardado, usamos los valores por defecto (tus claves de test).
    public static String API_KEY = prefs.get(KEY_API_KEY, "test_5e1qffwp4kcg6x6a8kni60p36_prueba");
    public static String API_SECRET = prefs.get(KEY_API_SECRET, "P6IsEgkapDZjaoWQLKARPr1kd1zyp64If1dNfcUWM8n");

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

    // Estas variables ahora serán dinámicas
    public static String INVOICES = "";
    public static String CREATE_INVOICE = "";
    public static String CREATE_CLIENT = "";
    public static String CREATE_SIGNER = "";

    /**
     * NUEVO MÉTODO: Guarda las credenciales en el sistema y actualiza la app.
     *
     * @param nuevaKey La nueva API Key
     * @param nuevoSecret El nuevo API Secret
     */
    public static void guardarCredenciales(String nuevaKey, String nuevoSecret) {
        // 1. Actualizar variables en memoria para uso inmediato
        API_KEY = nuevaKey;
        API_SECRET = nuevoSecret;

        // 2. Guardar permanentemente en el sistema (Preferences)
        prefs.put(KEY_API_KEY, nuevaKey);
        prefs.put(KEY_API_SECRET, nuevoSecret);

        System.out.println(">>> Config: Credenciales guardadas correctamente.");

        // 3. Forzar reinicio de sesión: Ponemos clientID a null
        // Esto hará que la próxima vez que se llame a la API, se autentique de nuevo con las nuevas claves.
        clientID = null;

        // 4. Recalcular rutas
        refrescarUUID();
    }

    /**
     * Este método debe llamarse UNA VEZ al iniciar el programa y cada vez que
     * se necesite una nueva transacción.
     */
    public static void refrescarUUID() {
        // 1. Obtener el ClientID de forma segura si no existe
        if (clientID == null || clientID.isEmpty() || clientID.equals("null")) {
            System.out.println(">>> Config: Recuperando ClientID (posible cambio de credenciales)...");
            // Llamamos al método directamente. 
            // NOTA: Asegúrate de que Clients.getFirstClientID() use Config.API_KEY y Config.API_SECRET
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

        System.out.println(">>> Config refrescada con éxito. ClientID activo: " + clientID);
    }
}

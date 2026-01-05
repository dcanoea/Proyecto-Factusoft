package com.mycompany.interfaz;

import com.mycompany.dao.ClienteDAO;
import com.mycompany.dominio.Cliente;
import com.mycompany.util.HibernateUtil;

/**
 *
 * @author David CE
 */
public class Main {

    public static void main(String[] args) {
        // Instalamos el tema FlatLaf Light (o Dark)
        try {
            // Puedes elegir: FlatLightLaf.setup() o FlatDarkLaf.setup()
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        System.out.println("Iniciando prueba de conexión...");

        // 1. Intentar conectar
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("¡Conexión a Hibernate exitosa!");
        } catch (Exception e) {
            System.err.println("Fallo al conectar: " + e.getMessage());
            return;
        }

        // 2. Intentar guardar un cliente de prueba
        ClienteDAO dao = new ClienteDAO();
        Cliente c = new Cliente();
        c.setFiscalName("David Cano Escario");
        c.setFiscalNumber("18053094A");
        c.setAddress("Paseo Ramon y Cajal");
        c.setCity("Huesca");
        c.setZipCode("22006");
        c.setPhone("680196618");
        c.setEmail("dcanoea@fpvirtualaragon.com");

        System.out.println("Guardando cliente...");
        dao.guardar(c);

        System.out.println("¡Cliente guardado! Revisa tu base de datos.");

        // Cierra Hibernate al terminar la prueba
        HibernateUtil.shutdown();
    }
}

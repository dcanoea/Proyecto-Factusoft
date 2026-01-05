package com.mycompany.interfaz;

import com.mycompany.dao.ClienteDAO;
import com.mycompany.dao.ProductoDAO;
import com.mycompany.dominio.Cliente;
import com.mycompany.dominio.Producto;
import com.mycompany.util.HibernateUtil;
import java.math.BigDecimal;

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

        System.out.println("--- Iniciando carga de productos de prueba ---");

        ProductoDAO productoDAO = new ProductoDAO();

        // 1. Crear Producto 1 (Servicio)
        Producto p1 = new Producto();
        p1.setCode("SERV-001");
        p1.setDescription("Consultoría Informática (Hora)");
        p1.setUnitPrice(new BigDecimal("45.00")); // Importante: BigDecimal con String
        p1.setTaxPercent(new BigDecimal("21.00"));
        p1.setActive(true);

        // 2. Crear Producto 2 (Material)
        Producto p2 = new Producto();
        p2.setCode("MAT-101");
        p2.setDescription("Cable de Red CAT6 (Metro)");
        p2.setUnitPrice(new BigDecimal("1.50"));
        p2.setTaxPercent(new BigDecimal("21.00"));
        p2.setActive(true);

        // 3. Crear Producto 3 (Sin IVA ejemplo)
        Producto p3 = new Producto();
        p3.setCode("EDU-005");
        p3.setDescription("Formación Exenta");
        p3.setUnitPrice(new BigDecimal("150.00"));
        p3.setTaxPercent(new BigDecimal("0.00"));
        p3.setActive(true);

        // Guardamos
        productoDAO.guardar(p1);
        productoDAO.guardar(p2);
        productoDAO.guardar(p3);

        System.out.println("¡3 Productos guardados correctamente!");

        // Cerramos hibernate
        HibernateUtil.shutdown();
    }
}

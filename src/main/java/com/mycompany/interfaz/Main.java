package com.mycompany.interfaz;


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

        // Aquí sigue el código normal de NetBeans para arrancar la ventana
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}

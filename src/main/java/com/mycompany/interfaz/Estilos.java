/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.interfaz;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author poker
 */
public class Estilos {

    // --- 1. PALETA DE COLORES ---
    public static final Color COLOR_FONDO_MENTA = Color.decode("#CBF6E3"); // Fondo principal
    public static final Color COLOR_VERDE_CLARO = Color.decode("#A0EECC"); // Acentos claros
    public static final Color COLOR_VERDE_FUERTE = Color.decode("#75E6B5"); // Acentos fuertes
    public static final Color COLOR_NEGRO_SUAVE = Color.decode("#111111"); // Textos principales
    public static final Color COLOR_GRIS_OSCURO = Color.decode("#2A2A2A"); // Bordes o secundarios
    public static final Color COLOR_NEGRO_PURO = Color.decode("#000000"); // Hover y contrastes
    public static final Color COLOR_BLANCO = Color.decode("#FFFFFF");

    // --- 2. FUENTES ---
    public static final Font FUENTE_TITULO = new Font("Century Gothic", Font.BOLD, 24);
    public static final Font FUENTE_MENU = new Font("Century Gothic", Font.PLAIN, 28);
    public static final Font FUENTE_TEXTO = new Font("Century Gothic", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON = new Font("Century Gothic", Font.BOLD, 14);

    // --- 3. MÉTODO PARA BOTONES DEL MENÚ LATERAL ---
    /**
     * Configura un botón para que parezca una opción de menú: - Alineado a la
     * izquierda. - Sin bordes. - Al pasar el ratón: Fondo Negro, Letra Blanca,
     * Icono Blanco.
     */
    public static void configurarBotonMenu(JButton boton, String texto, String rutaIconoSvg) {
        // Texto y Fuente
        boton.setText(texto);
        boton.setFont(FUENTE_MENU);
        boton.setForeground(COLOR_NEGRO_PURO);

        // Color de Fondo base (Menta)
        boton.setBackground(COLOR_FONDO_MENTA);

        // Comportamiento visual
        boton.setBorderPainted(false); // Sin borde
        boton.setFocusPainted(false);  // Sin recuadro de foco
        boton.setOpaque(true);         // Para que se pinte el fondo
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Manita al pasar ratón

        // Aumentar el espacio entre icono y texto
        boton.setIconTextGap(30);

        // Alineación (Izquierda + Espacio)
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(15);

        // --- ICONOS (SVG) ---
        // Tamaño estándar para iconos de menú
        int tamaño = 50;

        // 1. Icono Normal (Tal cual viene en el SVG, asumimos color negro)
        FlatSVGIcon iconoNormal = new FlatSVGIcon(rutaIconoSvg, tamaño, tamaño);

        // 2. Icono Hover (Lo transformamos a BLANCO con un filtro)
        FlatSVGIcon iconoBlanco = new FlatSVGIcon(rutaIconoSvg, tamaño, tamaño);
        iconoBlanco.setColorFilter(new FlatSVGIcon.ColorFilter(color -> COLOR_BLANCO));

        boton.setIcon(iconoNormal);
        boton.setRolloverIcon(iconoBlanco); // Se activa al pasar el ratón

        // --- ESTILO FLATLAF (HOVER) ---
        // Al hacer HOVER (pasar por encima) de un botón se muestra el fondo negro y las fuentes blanco
        boton.putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground: #000000;" // Fondo negro al pasar ratón
                + "hoverForeground: #FFFFFF;" // Texto blanco al pasar ratón
                + "arc: 0;" // Bordes cuadrados (franja)
                + "margin: 20,15,20,15;" // Margen interno (Arriba, Izq, Abajo, Der)
        );
    }

    // --- 4. MÉTODO PARA CAMPOS DE TEXTO Y JCOMBOBOX(Login y Formularios) ---
    /**
     * Aplica el estilo "Hacker/Moderno": Fondo Negro, Texto Blanco, Selección
     * Menta.
     */
    public static void aplicarEstiloCampo(JTextField campo) {
        // Estilo visual (Bordes y Márgenes)
        campo.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 999; " // Redondeado total
                + "focusedBorderColor: #CBF6E3; " // Borde menta al enfocar
                + "borderWidth: 1; "
                + "margin: 0,10,0,0" // Margen izquierdo para el texto
        );

        // Colores estándar Swing
        campo.setBackground(COLOR_NEGRO_PURO);
        campo.setForeground(COLOR_BLANCO);
        campo.setCaretColor(COLOR_BLANCO);        // Cursor blanco
        campo.setSelectionColor(COLOR_FONDO_MENTA); // Selección verde menta
        campo.setSelectedTextColor(COLOR_NEGRO_PURO);
    }

    public static void aplicarEstiloComboBox(javax.swing.JComboBox combo) {
        // 1. COLORES DEL BOTÓN (Estado normal cerrado)
        combo.setBackground(java.awt.Color.WHITE);
        combo.setForeground(com.mycompany.interfaz.Estilos.COLOR_NEGRO_PURO);

        // 2. ESTILO VISUAL DEL BOTÓN (FlatLaf)
        combo.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, ""
                + "arc: 999; " // Redondeado
                + "borderWidth: 1; "
                + "borderColor: #cccccc; "
                + "focusedBorderColor: #CBF6E3; "
                + "padding: 6,10,6,10; " // Altura para igualar al TextField
                + "arrowType: chevron; "
                + "buttonArrowColor: #000000; "
                + "buttonBackground: #FFFFFF; "
                + "buttonSeparatorWidth: 0; "
        );

        // 3. PERSONALIZACIÓN DE LA LISTA DESPLEGABLE (POPUP)
        // Aquí definimos el color Blanco de fondo y Verde Oscuro de selección
        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // --- CONFIGURACIÓN DE COLORES DE LA LISTA ---
                if (isSelected) {
                    // CASO 1: Cuando pasas el ratón por encima (Seleccionado)
                    setBackground(COLOR_VERDE_FUERTE);
                    setForeground(COLOR_NEGRO_PURO);
                } else {
                    // CASO 2: Elemento normal (No seleccionado)
                    setBackground(COLOR_BLANCO);
                    setForeground(COLOR_NEGRO_PURO);
                }

                // Margen interno para que el texto de la lista no quede pegado al borde
                setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

                return this;
            }
        });
    }

    // --- 5. ESTILOS PARA FORMULARIOS (PANELES CREAR CLIENTE, PRODUCTO) ---
    public static void configurarEtiquetaFormulario(javax.swing.JLabel lbl) {
        // Recuperamos Century Gothic, negrita suave y tamaño 14
        lbl.setFont(new Font("Century Gothic", Font.BOLD, 14));
        lbl.setForeground(Color.decode("#333333")); // Gris oscuro
    }

    public static void configurarCampoFormulario(JTextField campo) {
        // Estilo: Borde gris suave, y al enfocar se pone VERDE MENTA (#75E6B5)
        campo.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 10; " // Redondeado suave
                + "borderWidth: 1; " // Borde fino
                + "borderColor: #a8a8a8; " // Gris medio en reposo
                + "focusWidth: 1; " // Grosor del borde al enfocar (fino, sin halo gigante)
                + "focusedBorderColor: #75E6B5; " // <--- TEMA MENTA (Adiós azul)
                + "margin: 5, 10, 5, 10;" // Padding interno (altura)
        );

        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setCaretColor(Color.BLACK);

        // Fuente Century Gothic
        campo.setFont(new Font("Century Gothic", Font.PLAIN, 14));
    }

    public static void configurarTabla(javax.swing.JTable tabla, javax.swing.JScrollPane scroll) {
        // 1. CONFIGURACIÓN VISUAL BÁSICA (Lo que arregla el fondo gris)
        tabla.setBackground(COLOR_FONDO_MENTA);      // <--- ESTO ARREGLA EL FONDO GRIS DE LA TABLA
        tabla.setFillsViewportHeight(true);   // <--- ESTO HACE QUE LA TABLA OCUPE TODO EL ALTO
        tabla.setRowHeight(40);
        tabla.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

        // Desactivar selección de celdas individuales
        tabla.setCellSelectionEnabled(false);
        tabla.setColumnSelectionAllowed(false);
        tabla.setRowSelectionAllowed(true);

        // 2. RENDERER (PINTOR) PERSONALIZADO
        // Se encarga de pintar las filas de negro al seleccionarlas y verde si no
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            private final javax.swing.border.Border padding = javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10);

            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Truco: hasFocus = false para que no pinte el borde de foco
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

                if (isSelected) {
                    setBackground(java.awt.Color.BLACK);
                    setForeground(java.awt.Color.WHITE);
                } else {
                    setBackground(COLOR_FONDO_MENTA); // Verde para las filas normales
                    setForeground(java.awt.Color.BLACK);
                }

                setBorder(padding);
                return this;
            }
        });

        // 3. LIMPIEZA (Quitar rejillas)
        tabla.setShowGrid(false);
        tabla.setShowHorizontalLines(false);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new java.awt.Dimension(0, 0));

        // 4. CABECERA
        javax.swing.table.JTableHeader header = tabla.getTableHeader();
        header.setBackground(COLOR_VERDE_FUERTE); // Verde un poco más oscuro
        header.setForeground(java.awt.Color.BLACK);
        header.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.BLACK));
        header.setReorderingAllowed(false);

        // 5. SCROLLPANE (FONDO VERDE TAMBIÉN)
        if (scroll != null) {
            scroll.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 1));
            scroll.getViewport().setBackground(COLOR_FONDO_MENTA); // <--- ESTO ARREGLA EL HUECO SOBRANTE
        }
    }

    public static void configurarBarraBusqueda(javax.swing.JTextField txt) {
        txt.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, ""
                + "arc: 999;" // Redondo
                + "margin: 5,15,5,15;" // Márgenes internos
                + "borderWidth: 0;" // Sin borde gris
                + "background: #FFFFFF;" // Blanco puro
        );
        txt.putClientProperty(com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new com.formdev.flatlaf.extras.FlatSVGIcon("img/search.svg"));
    }

    public static void configurarBotonAccion(javax.swing.JButton btn) {
        btn.setBackground(COLOR_NEGRO_SUAVE);
        btn.setForeground(Color.WHITE);
        btn.setFont(FUENTE_BOTON);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Estilo FlatLaf Píldora
        btn.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, ""
                + "arc: 999;" // Redondo total
                + "borderWidth: 0;" // Sin borde
                + "focusWidth: 0;" // Sin halo azul
                + "innerFocusWidth: 0;"
                + "margin: 8,15,8,15;" // Márgenes internos
        );
    }
}

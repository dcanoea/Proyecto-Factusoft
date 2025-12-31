/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.interfaz;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

/**
 *
 * @author DavidCe
 */
public class PanelCrearFactura extends javax.swing.JPanel {

    // Variables para guardar los datos recibidos
    private String nombreCliente;
    private String tipoFactura;

    /**
     * Constructor modificado para recibir DATOS
     */
    public PanelCrearFactura(String nombreCliente, String tipoFactura) {
        this.nombreCliente = nombreCliente;
        this.tipoFactura = tipoFactura;

        initComponents();
        configurarEstilos();
        cargarDatos();
    }

    public PanelCrearFactura() {
        this("Cliente Prueba", "Completa");
    }

    private void configurarEstilos() {
        // --- 1. FONDO GENERAL ---
        Color colorMenta = Estilos.COLOR_FONDO_MENTA;
        this.setBackground(colorMenta);
        jPanelCenter.setBackground(colorMenta);

        // --- 2. SOLUCIÓN PANEL DERECHO (ADIÓS GRIS) ---
        // IMPORTANTE: Lo ponemos OPACO (true) para obligarle a pintar el color Menta
        // Si lo ponemos transparente (false), a veces Swing pinta gris por debajo.
        jPanelRight.setOpaque(true);
        jPanelRight.setBackground(Estilos.COLOR_VERDE_CLARO);

        // --- 3. ICONOS Y BOTONES ---
        JButton[] botones = {btnAddLine, btnEditLine, btnDeleteLine, btnSendInvoice, btnBack};
        for (JButton btn : botones) {
            Estilos.configurarBotonAccion(btn);
            btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            btn.setMargin(new Insets(10, 15, 10, 15));
            btn.setIconTextGap(15);
        }

        setIconoBlanco(btnAddLine, "img/add_Icon.svg");
        setIconoBlanco(btnEditLine, "img/edit_Icon.svg");
        setIconoBlanco(btnDeleteLine, "img/delete_Icon.svg");
        setIconoBlanco(btnSendInvoice, "img/invoice_Icon.svg");

        // --- 4. ESTILOS DE TABLAS ---
        Estilos.configurarTabla(tblCabeceraCliente, jScrollPaneCabecera);
        Estilos.configurarTabla(tblInvoices, jScrollPaneCenter);
        Estilos.configurarTabla(jTableTotal, jScrollPaneTotalFactura);

        // Alturas de fila
        tblCabeceraCliente.setRowHeight(40);
        tblInvoices.setRowHeight(35);
        jTableTotal.setRowHeight(40);

        // Anchos de columna
        if (tblInvoices.getColumnModel().getColumnCount() > 0) {
            tblInvoices.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblInvoices.getColumnModel().getColumn(1).setPreferredWidth(300);
            tblInvoices.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        // --- 5. SOLUCIÓN BORDES Y SCROLLBARS ---
        // A) Dimensiones fijas (para que no se aplasten)
        Dimension dimFija = new Dimension(100, 85);
        jScrollPaneCabecera.setPreferredSize(dimFija);
        jScrollPaneCabecera.setMinimumSize(dimFija);
        jScrollPaneTotalFactura.setPreferredSize(dimFija);
        jScrollPaneTotalFactura.setMinimumSize(dimFija);

        // B) Quitar el "Scroll Negro" y los Bordes
        // Desactivamos las barras de scroll para Cabecera y Totales (nunca deben tener scroll)
        jScrollPaneCabecera.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneCabecera.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jScrollPaneTotalFactura.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneTotalFactura.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // C) Huecos entre tablas
        // Usamos borde vacío para empujar los elementos y crear espacio
        javax.swing.border.Border huecoAbajo = javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0); // 20px margen inferior

        jScrollPaneCabecera.setBorder(huecoAbajo); // Separa Cabecera de Central
        jScrollPaneCenter.setBorder(huecoAbajo);   // Separa Central de Totales
        // Totales no necesita hueco abajo

        // D) Fondo unificado
        // Aseguramos que el fondo detrás de las tablas sea Menta
        jScrollPaneCabecera.getViewport().setBackground(colorMenta);
        jScrollPaneCenter.getViewport().setBackground(colorMenta);
        jScrollPaneTotalFactura.getViewport().setBackground(colorMenta);
    }

    private void cargarDatos() {
        if (tblCabeceraCliente.getRowCount() > 0) {
            tblCabeceraCliente.setValueAt(nombreCliente, 0, 0);
            tblCabeceraCliente.setValueAt("B-12345678", 0, 1);
        }
    }

    private void setIconoBlanco(JButton btn, String rutaSvg) {
        FlatSVGIcon icon = new FlatSVGIcon(rutaSvg, 20, 20);
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> Color.WHITE));
        btn.setIcon(icon);
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelCenter = new javax.swing.JPanel();
        jScrollPaneCabecera = new javax.swing.JScrollPane();
        tblCabeceraCliente = new javax.swing.JTable();
        jScrollPaneCenter = new javax.swing.JScrollPane();
        tblInvoices = new javax.swing.JTable();
        jScrollPaneTotalFactura = new javax.swing.JScrollPane();
        jTableTotal = new javax.swing.JTable();
        jPanelRight = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        btnAddLine = new javax.swing.JButton();
        btnEditLine = new javax.swing.JButton();
        btnDeleteLine = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        btnSendInvoice = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        setBackground(new java.awt.Color(160, 238, 204));
        setLayout(new java.awt.BorderLayout());

        jPanelCenter.setBackground(new java.awt.Color(160, 238, 204));
        jPanelCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelCenter.setLayout(new java.awt.GridBagLayout());

        jScrollPaneCabecera.setMinimumSize(new java.awt.Dimension(100, 60));
        jScrollPaneCabecera.setPreferredSize(new java.awt.Dimension(100, 60));

        tblCabeceraCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tblCabeceraCliente.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Nombre ", "DNI/NIE/CIF"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneCabecera.setViewportView(tblCabeceraCliente);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 15, 0);
        jPanelCenter.add(jScrollPaneCabecera, gridBagConstraints);

        jScrollPaneCenter.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPaneCenter.setOpaque(false);

        tblInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Descripción", "Cantidad", "Precio Ud.", "Descuento", "% IVA", "Total Línea"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneCenter.setViewportView(tblInvoices);
        if (tblInvoices.getColumnModel().getColumnCount() > 0) {
            tblInvoices.getColumnModel().getColumn(0).setResizable(false);
            tblInvoices.getColumnModel().getColumn(1).setResizable(false);
            tblInvoices.getColumnModel().getColumn(2).setResizable(false);
            tblInvoices.getColumnModel().getColumn(3).setResizable(false);
            tblInvoices.getColumnModel().getColumn(4).setResizable(false);
            tblInvoices.getColumnModel().getColumn(5).setResizable(false);
            tblInvoices.getColumnModel().getColumn(6).setResizable(false);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelCenter.add(jScrollPaneCenter, gridBagConstraints);

        jScrollPaneTotalFactura.setMinimumSize(new java.awt.Dimension(100, 85));
        jScrollPaneTotalFactura.setPreferredSize(new java.awt.Dimension(100, 85));

        jTableTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Base Imponible", "Tipo IVA", "Importe IVA", "TOTAL FACTURA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneTotalFactura.setViewportView(jTableTotal);
        if (jTableTotal.getColumnModel().getColumnCount() > 0) {
            jTableTotal.getColumnModel().getColumn(0).setResizable(false);
            jTableTotal.getColumnModel().getColumn(1).setResizable(false);
            jTableTotal.getColumnModel().getColumn(2).setResizable(false);
            jTableTotal.getColumnModel().getColumn(3).setResizable(false);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelCenter.add(jScrollPaneTotalFactura, gridBagConstraints);

        add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelRight.setOpaque(false);
        jPanelRight.setPreferredSize(new java.awt.Dimension(200, 0));
        jPanelRight.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        jPanelRight.add(filler1, gridBagConstraints);

        btnAddLine.setText("Agregar Línea");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnAddLine, gridBagConstraints);

        btnEditLine.setText("Editar Línea");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnEditLine, gridBagConstraints);

        btnDeleteLine.setText("Eliminar Línea");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnDeleteLine, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        jPanelRight.add(filler2, gridBagConstraints);

        btnSendInvoice.setText("Envíar Factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnSendInvoice, gridBagConstraints);

        btnBack.setText("Volver");
        btnBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBack.addActionListener(this::btnBackActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnBack, gridBagConstraints);

        add(jPanelRight, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // 1. Obtener el contenedor padre (el panel principal del Dashboard)
        java.awt.Container parent = this.getParent();

        if (parent != null) {
            // 2. Limpiar el contenido actual (Quitar PanelCrearFactura)
            parent.removeAll();

            // 3. Añadir el panel de la lista de facturas
            // Asegúrate de que 'PanelFacturas' es el nombre correcto de tu clase
            parent.add(new PanelFacturas(), java.awt.BorderLayout.CENTER);

            // 4. Refrescar la interfaz para mostrar el cambio
            parent.revalidate();
            parent.repaint();
        }
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddLine;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDeleteLine;
    private javax.swing.JButton btnEditLine;
    private javax.swing.JButton btnSendInvoice;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JScrollPane jScrollPaneCabecera;
    private javax.swing.JScrollPane jScrollPaneCenter;
    private javax.swing.JScrollPane jScrollPaneTotalFactura;
    private javax.swing.JTable jTableTotal;
    private javax.swing.JTable tblCabeceraCliente;
    private javax.swing.JTable tblInvoices;
    // End of variables declaration//GEN-END:variables

}

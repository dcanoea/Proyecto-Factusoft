/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.interfaz;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.mycompany.dominio.Cliente;
import com.mycompany.dominio.Producto;
import com.mycompany.fiskaly.Invoices.CreateCompleteInvoice;
import com.mycompany.fiskaly.Invoices.DTO.CategoryDTO;
import com.mycompany.fiskaly.Invoices.DTO.CategoryDTO.Rate;
import com.mycompany.fiskaly.Invoices.DTO.ContentCompleteDTO;
import com.mycompany.fiskaly.Invoices.DTO.DataDTO;
import com.mycompany.fiskaly.Invoices.DTO.IdDTO;
import com.mycompany.fiskaly.Invoices.DTO.ItemDTO;
import com.mycompany.fiskaly.Invoices.DTO.ItemDTO.VatType;
import com.mycompany.fiskaly.Invoices.DTO.RecipientsDTO;
import com.mycompany.fiskaly.Invoices.DTO.SystemDTO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import static javax.swing.SwingUtilities.invokeLater;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DavidCe
 */
public class PanelCrearFactura extends javax.swing.JPanel {

    // Variables para guardar los datos recibidos
    private String tipoFactura;
    private Cliente cliente;

    /**
     * Constructor que recibe el Cliente
     */
    public PanelCrearFactura(com.mycompany.dominio.Cliente cliente, String tipoFactura) {
        this.cliente = cliente;
        this.tipoFactura = tipoFactura;

        initComponents();
        configurarEstilos();
        cargarDatos();
    }

    private void configurarEstilos() {
        Color colorMenta = Estilos.COLOR_FONDO_MENTA;
        this.setBackground(colorMenta);
        jPanelCenter.setBackground(colorMenta);
        jPanelRight.setOpaque(true);
        jPanelRight.setBackground(colorMenta);

        // --- ICONOS Y BOTONES ---
        FlatSVGIcon homeIcon = new FlatSVGIcon("img/home_Icon.svg", 32, 32);
        homeIcon.setColorFilter(new FlatSVGIcon.ColorFilter(c -> Estilos.COLOR_NEGRO_PURO));

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

        // --- CONFIGURACIÓN DE TABLAS ---
        Estilos.configurarTabla(tblCabeceraCliente, jScrollPaneCabecera);
        Estilos.configurarTabla(tblInvoices, jScrollPaneCenter);
        Estilos.configurarTabla(jTableTotal, jScrollPaneTotalFactura);

        // >>> CORRECCIÓN: LIMPIAR LAS FILAS VACÍAS DE NETBEANS <<<
        // 1. Tabla Central (Facturas): La dejamos vacía (0 filas)
        javax.swing.table.DefaultTableModel modelInvoices = (javax.swing.table.DefaultTableModel) tblInvoices.getModel();
        modelInvoices.setRowCount(0);

        // 2. Tabla Inferior (Totales): La limpiamos y añadimos 1 fila inicial a cero
        javax.swing.table.DefaultTableModel modelTotal = (javax.swing.table.DefaultTableModel) jTableTotal.getModel();
        modelTotal.setRowCount(0);
        modelTotal.addRow(new Object[]{"0.00 €", "Varios", "0.00 €", "0.00 €"});

        // --- TAMAÑOS Y SCROLLS ---
        tblCabeceraCliente.setRowHeight(40);
        tblInvoices.setRowHeight(35);
        jTableTotal.setRowHeight(40);

        if (tblInvoices.getColumnModel().getColumnCount() > 0) {
            tblInvoices.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblInvoices.getColumnModel().getColumn(1).setPreferredWidth(300);
            tblInvoices.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        Dimension dimFija = new Dimension(100, 85);
        jScrollPaneCabecera.setPreferredSize(dimFija);
        jScrollPaneCabecera.setMinimumSize(dimFija);
        jScrollPaneTotalFactura.setPreferredSize(dimFija);
        jScrollPaneTotalFactura.setMinimumSize(dimFija);

        jScrollPaneCabecera.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneCabecera.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneTotalFactura.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPaneTotalFactura.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jScrollPaneCabecera.getViewport().setBackground(colorMenta);
        jScrollPaneCenter.getViewport().setBackground(colorMenta);
        jScrollPaneTotalFactura.getViewport().setBackground(colorMenta);
    }

    private void cargarDatos() {
        if (cliente != null && tblCabeceraCliente.getRowCount() > 0) {

            // 1. Calculamos la serie actual (F26)
            String serieActual = obtenerSerieConAnio();

            // 2. Preguntamos a la BBDD Local cuál toca
            com.mycompany.dao.FacturaDAO facturaDao = new com.mycompany.dao.FacturaDAO();

            // El DAO buscará el MAX(number) donde series = 'F26'
            // Si no hay ninguna, devolverá "F26-0001"
            String numeroPrevisto = facturaDao.getSiguienteNumeroFactura(serieActual);

            // 3. Lo pintamos en la tabla
            tblCabeceraCliente.setValueAt(numeroPrevisto, 0, 0);

            // 4. Rellenamos resto de datos del cliente
            tblCabeceraCliente.setValueAt(this.tipoFactura, 0, 1);
            tblCabeceraCliente.setValueAt(cliente.getFiscalName(), 0, 2);
            tblCabeceraCliente.setValueAt(cliente.getFiscalNumber(), 0, 3);
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
                {null, null, null, null}
            },
            new String [] {
                "Nº Factura", "Tipo Factura", "Nombre ", "DNI/NIE/CIF"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneCabecera.setViewportView(tblCabeceraCliente);
        if (tblCabeceraCliente.getColumnModel().getColumnCount() > 0) {
            tblCabeceraCliente.getColumnModel().getColumn(0).setResizable(false);
            tblCabeceraCliente.getColumnModel().getColumn(1).setResizable(false);
            tblCabeceraCliente.getColumnModel().getColumn(2).setResizable(false);
            tblCabeceraCliente.getColumnModel().getColumn(3).setResizable(false);
        }

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
        btnAddLine.addActionListener(this::btnAddLineActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnAddLine, gridBagConstraints);

        btnEditLine.setText("Editar Línea");
        btnEditLine.addActionListener(this::btnEditLineActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnEditLine, gridBagConstraints);

        btnDeleteLine.setText("Eliminar Línea");
        btnDeleteLine.addActionListener(this::btnDeleteLineActionPerformed);
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
        btnSendInvoice.addActionListener(this::btnSendInvoiceActionPerformed);
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

    private void btnAddLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLineActionPerformed
        agregarLineaProducto();
    }//GEN-LAST:event_btnAddLineActionPerformed

    private void btnDeleteLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLineActionPerformed
        eliminarLineaSeleccionada();
    }//GEN-LAST:event_btnDeleteLineActionPerformed

    private void btnEditLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditLineActionPerformed
        editarLineaSeleccionada();    }//GEN-LAST:event_btnEditLineActionPerformed

    private void btnSendInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendInvoiceActionPerformed
        // ---------------------------------------------------------
        // 1. VALIDACIONES PREVIAS
        // ---------------------------------------------------------
        if (this.cliente == null) {
            JOptionPane.showMessageDialog(this, "Error: No hay cliente asignado.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblInvoices.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "La factura no tiene líneas.");
            return;
        }

        // Bloqueamos el botón temporalmente
        btnSendInvoice.setEnabled(false);
        btnSendInvoice.setText("Enviando...");

        // ---------------------------------------------------------
        // 2. PREPARACIÓN DE DATOS (DTOs)
        // ---------------------------------------------------------
        // A. RECIPIENTS (CLIENTE)
        IdDTO idDto = new IdDTO(cliente.getFiscalNumber(), true, cliente.getFiscalName());

        // >>> CORRECCIÓN AQUÍ: Incluir la Ciudad <<<
        // Como el constructor de RecipientsDTO parece aceptar solo (Dirección, ID, Zip),
        // concatenamos la ciudad a la dirección para que aparezca en el PDF.
        // Asegúrate de que tu clase Cliente tiene el método .getCity() o .getPoblacion()
        String direccionCompleta = cliente.getAddress();
        if (cliente.getCity() != null && !cliente.getCity().isEmpty()) {
            direccionCompleta += ", " + cliente.getCity();
        }

        // Pasamos la dirección modificada (Ej: "Paseo las Damas, Zaragoza")
        RecipientsDTO recipients = new RecipientsDTO(direccionCompleta, idDto, cliente.getZipCode());

        java.util.List<RecipientsDTO> listaRecipients = new java.util.ArrayList<>();
        listaRecipients.add(recipients);

        // B. ITEMS (LÍNEAS)
        java.util.List<ItemDTO> listaItems = new java.util.ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                String descripcion = model.getValueAt(i, 1).toString();
                BigDecimal cantidad = new BigDecimal(model.getValueAt(i, 2).toString().replace(",", "."));
                BigDecimal precioUnit = new BigDecimal(model.getValueAt(i, 3).toString().replace(",", "."));
                BigDecimal descuentoPorc = new BigDecimal(model.getValueAt(i, 4).toString().replace(",", "."));
                BigDecimal ivaPorc = new BigDecimal(model.getValueAt(i, 5).toString().replace(",", "."));

                // Cálculo Descuento en Euros para la API
                BigDecimal descuentoEuros = precioUnit.multiply(descuentoPorc)
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                Rate rateEnum = obtenerRateSegunIva(ivaPorc);
                CategoryDTO category = new CategoryDTO(rateEnum);
                SystemDTO system = new SystemDTO(SystemDTO.Type.REGULAR, category);

                ItemDTO item = new ItemDTO(
                        String.format(Locale.US, "%.2f", cantidad),
                        system,
                        String.format(Locale.US, "%.2f", descuentoEuros),
                        descripcion,
                        String.format(Locale.US, "%.2f", precioUnit),
                        VatType.IVA
                );
                listaItems.add(item);
            } catch (Exception e) {
                System.err.println("Error procesando fila " + i + ": " + e.getMessage());
            }
        }

        // C. NUMERACIÓN
        String numeroCompleto = tblCabeceraCliente.getValueAt(0, 0).toString();

        DataDTO dataDto = new DataDTO(numeroCompleto, "Factura de Venta", listaItems);
        ContentCompleteDTO contentComplete = new ContentCompleteDTO(dataDto, listaRecipients);
        
        // ---------------------------------------------------------
        // 3. ENVÍO Y GUARDADO (HILO SECUNDARIO)
        // ---------------------------------------------------------

        // Guardamos las listas en variables finales para usarlas dentro del hilo
        final java.util.List<ItemDTO> finalItems = listaItems;
        final java.util.List<RecipientsDTO> finalRecipients = listaRecipients;

        new Thread(() -> {
            try {
                // 1. SINCRONIZACIÓN: Preguntamos a la nube cuál es el número REAL ahora mismo
                // (Esto tarda unos milisegundos, por eso va en el hilo)
                com.mycompany.fiskaly.Config.refrescarUUID(); // Refresco credenciales

                // ¡AQUÍ ESTÁ LA MAGIA! Calculamos el número fresco
                String serie = "F"; // O la que uses dinámicamente
                String numeroOficial = com.mycompany.fiskaly.Invoices.InvoicesManagement.getGlobalNextInvoiceNumber(serie);

                if (numeroOficial == null) {
                    throw new Exception("No se pudo conectar con Fiskaly para sincronizar el número.");
                }

                System.out.println(">>> Número sincronizado asignado: " + numeroOficial);

                // 2. RE-CREAMOS LOS DTOs CON EL NÚMERO NUEVO
                // No usamos el de la tabla, usamos 'numeroOficial'
                DataDTO dataDtoFinal = new DataDTO(numeroOficial, "Factura de Venta", finalItems);
                ContentCompleteDTO contentCompleteFinal = new ContentCompleteDTO(dataDtoFinal, finalRecipients);

                // 3. LLAMADA A LA API (Con el número correcto)
                int statusCode = com.mycompany.fiskaly.Invoices.CreateCompleteInvoice.createInvoice(contentCompleteFinal);

                // 4. VOLVER AL HILO VISUAL
                javax.swing.SwingUtilities.invokeLater(() -> {

                    if (statusCode >= 200 && statusCode < 300) {
                        // === ÉXITO ===

                        // Actualizamos la tabla visualmente para que el usuario vea el cambio de número
                        tblCabeceraCliente.setValueAt(numeroOficial, 0, 0);

                        btnSendInvoice.setEnabled(false);
                        btnSendInvoice.setText("Enviada");

                        try {
                            String uuidUtilizado = com.mycompany.fiskaly.Config.random_UUID.toString();
                            com.mycompany.dominio.Factura nuevaFactura = new com.mycompany.dominio.Factura();

                            // Parseamos el número nuevo (Ej: F26-0016)
                            // Usamos lógica segura por si cambia el formato
                            String[] partes = numeroOficial.split("-");
                            nuevaFactura.setSeries(partes[0]); // Ej: F26
                            // Cogemos la última parte como número entero
                            nuevaFactura.setNumber(Integer.parseInt(partes[partes.length - 1]));

                            nuevaFactura.setDate(java.time.LocalDateTime.now());
                            nuevaFactura.setCliente(this.cliente);
                            nuevaFactura.setFiskalyUuid(uuidUtilizado);

                            // OJO CON EL PDF: Si lo generaste antes con el número viejo, 
                            // aquí intentará buscar el nuevo. 
                            // Lo ideal es regenerar el PDF aquí o aceptar que el nombre del archivo en escritorio sea el viejo.
                            // Para no romper tu código, intentamos leer con el numeroOficial:
                            try {
                                String userHome = System.getProperty("user.home");
                                // Intentamos buscar con el nuevo nombre
                                String pdfPath = userHome + "/Desktop/Factura_Completa_" + numeroOficial + ".pdf";
                                java.io.File pdfFile = new java.io.File(pdfPath);

                                // Si no existe (porque el PDF se generó con el numero viejo en la tabla),
                                // intentamos buscarlo con el numero viejo que había en la tabla antes de enviar
                                if (!pdfFile.exists()) {
                                    String numeroViejoTabla = tblCabeceraCliente.getValueAt(0, 0).toString(); // Ojo, esto lee del componente (riesgo leve)
                                    pdfPath = userHome + "/Desktop/Factura_Completa_" + numeroViejoTabla + ".pdf";
                                    pdfFile = new java.io.File(pdfPath);
                                }

                                if (pdfFile.exists()) {
                                    byte[] pdfBytes = java.nio.file.Files.readAllBytes(pdfFile.toPath());
                                    String pdfBase64 = java.util.Base64.getEncoder().encodeToString(pdfBytes);
                                    nuevaFactura.setPdfFactura(pdfBase64);
                                } else {
                                    nuevaFactura.setPdfFactura(null);
                                }
                            } catch (Exception exPdf) {
                                exPdf.printStackTrace();
                            }

                            // ... (TU CÓDIGO DE CÁLCULO DE TOTALES SIGUE IGUAL AQUÍ) ...
                            // Copia y pega tu bloque "CÁLCULO DE TOTALES PARA BBDD" exacto
                            // Solo asegúrate de usar 'nuevaFactura'
                            BigDecimal acumuladorBase = BigDecimal.ZERO;
                            BigDecimal acumuladorImpuestos = BigDecimal.ZERO;
                            java.util.List<com.mycompany.dominio.LineaFactura> lineasEntidad = new java.util.ArrayList<>();

                            for (int i = 0; i < model.getRowCount(); i++) {
                                com.mycompany.dominio.LineaFactura linea = new com.mycompany.dominio.LineaFactura();
                                String desc = model.getValueAt(i, 1).toString();
                                BigDecimal cant = new BigDecimal(model.getValueAt(i, 2).toString().replace(",", "."));
                                BigDecimal prec = new BigDecimal(model.getValueAt(i, 3).toString().replace(",", "."));
                                BigDecimal descPorc = new BigDecimal(model.getValueAt(i, 4).toString().replace(",", "."));
                                BigDecimal ivaPorc = new BigDecimal(model.getValueAt(i, 5).toString().replace(",", "."));
                                BigDecimal totLinea = new BigDecimal(model.getValueAt(i, 6).toString().replace(",", "."));

                                BigDecimal bruto = prec.multiply(cant);
                                BigDecimal descDinero = bruto.multiply(descPorc).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                                BigDecimal baseLinea = bruto.subtract(descDinero);
                                BigDecimal ivaDinero = baseLinea.multiply(ivaPorc).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                                acumuladorBase = acumuladorBase.add(baseLinea);
                                acumuladorImpuestos = acumuladorImpuestos.add(ivaDinero);

                                linea.setDescription(desc);
                                linea.setQuantity(cant);
                                linea.setUnitPrice(prec);
                                linea.setDiscountPercent(descPorc);
                                linea.setTaxPercent(ivaPorc);
                                linea.setTotalLine(totLinea);
                                linea.setFactura(nuevaFactura);
                                lineasEntidad.add(linea);
                            }

                            nuevaFactura.setTotalBase(acumuladorBase);
                            nuevaFactura.setTotalTax(acumuladorImpuestos);
                            // Usamos dataDtoFinal que tiene el numero correcto
                            // Ojo: fullAmount viene del DTO, hay que calcularlo o fiarse del DTO previo
                            // Como el total no cambia al cambiar el número, puedes recalcularlo:
                            BigDecimal totalFinal = acumuladorBase.add(acumuladorImpuestos);
                            nuevaFactura.setTotalAmount(totalFinal);

                            nuevaFactura.setLineas(lineasEntidad);

                            // GUARDAR EN MYSQL
                            com.mycompany.dao.FacturaDAO dao = new com.mycompany.dao.FacturaDAO();
                            dao.guardarFactura(nuevaFactura);

                            JOptionPane.showMessageDialog(this,
                                    "Factura " + numeroOficial + " (Sincronizada) guardada y enviada correctamente.",
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                        } catch (Exception dbEx) {
                            dbEx.printStackTrace();
                            JOptionPane.showMessageDialog(this,
                                    "Error al guardar en BBDD: " + dbEx.getMessage(),
                                    "Error BBDD", JOptionPane.WARNING_MESSAGE);
                        }

                    } else {
                        // Error API
                        btnSendInvoice.setEnabled(true);
                        btnSendInvoice.setText("Enviar Factura");
                        JOptionPane.showMessageDialog(this, "Error API Fiskaly. Código: " + statusCode, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    btnSendInvoice.setEnabled(true);
                    btnSendInvoice.setText("Enviar Factura");
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                });
            }
        }).start();
    }//GEN-LAST:event_btnSendInvoiceActionPerformed


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

    // --- MÉTODOS LÓGICOS PARA AGREGAR PRODUCTOS ---
// --- MÉTODOS LÓGICOS PARA AGREGAR PRODUCTOS ---
    private void agregarLineaProducto() {
        // 1. Abrir el buscador
        java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
        DialogBuscarProducto dialog = new DialogBuscarProducto((java.awt.Frame) parentWindow, true);
        dialog.setVisible(true);

        // 2. Obtener selección
        Producto producto = dialog.getProductoSeleccionado();

        if (producto != null) {
            // 3. Pedir cantidad
            String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad para: " + producto.getDescription(), "1");

            if (cantidadStr != null && !cantidadStr.isEmpty()) {
                try {
                    // Convertir a BigDecimal para precisión monetaria
                    BigDecimal cantidad = new BigDecimal(cantidadStr.replace(",", "."));
                    BigDecimal precio = producto.getUnitPrice(); // Viene de la BBDD
                    BigDecimal iva = producto.getTaxPercent();   // Viene de la BBDD
                    BigDecimal descuentoPorc = BigDecimal.ZERO;      // 0 por defecto al agregar

                    // --- CÁLCULO UNIFICADO (Lógica PDF Fiskaly) ---
                    // 1. Calcular el descuento en euros por UNIDAD (redondeado a 2 decimales)
                    BigDecimal descuentoUnitario = precio.multiply(descuentoPorc)
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    // 2. Calcular el precio neto unitario
                    BigDecimal precioNetoUnitario = precio.subtract(descuentoUnitario);

                    // 3. Calcular la Base Imponible de la línea (Precio Neto * Cantidad)
                    BigDecimal baseLinea = precioNetoUnitario.multiply(cantidad);

                    // 4. Calcular importe IVA sobre la base (redondeado a 2 decimales)
                    BigDecimal importeIva = baseLinea.multiply(iva)
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    // 5. Total Línea
                    BigDecimal totalLinea = baseLinea.add(importeIva);

                    // 4. Añadir a la tabla de líneas (tblInvoices)
                    DefaultTableModel model = (DefaultTableModel) tblInvoices.getModel();
                    model.addRow(new Object[]{
                        producto.getCode(),
                        producto.getDescription(),
                        cantidad, // Guardamos el número, no string
                        precio,
                        descuentoPorc,
                        iva,
                        totalLinea.setScale(2, RoundingMode.HALF_UP) // Redondeo a 2 decimales
                    });

                    // 5. Recalcular la tabla de abajo (Totales)
                    calcularTotalesFactura();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida. Introduce un número (ej: 1.5)");
                }
            }
        }
    }

    // --- MÉTODO PARA EDITAR LÍNEA ---
    private void editarLineaSeleccionada() {
        DefaultTableModel model = (DefaultTableModel) tblInvoices.getModel();
        int fila = tblInvoices.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una línea para editar.");
            return;
        }

        // 1. Recuperar valores actuales
        String descProducto = model.getValueAt(fila, 1).toString();
        // Usamos toString() para asegurar compatibilidad si el modelo tiene Numbers o Strings
        String cantStr = model.getValueAt(fila, 2).toString();
        String precStr = model.getValueAt(fila, 3).toString();
        String descStr = model.getValueAt(fila, 4).toString();
        String ivaStr = model.getValueAt(fila, 5).toString();

        // 2. Crear panel con formulario para el JOptionPane
        javax.swing.JPanel panelEdicion = new javax.swing.JPanel(new java.awt.GridLayout(3, 2, 10, 10));
        javax.swing.JTextField txtCantidad = new javax.swing.JTextField(cantStr);
        javax.swing.JTextField txtPrecio = new javax.swing.JTextField(precStr);
        javax.swing.JTextField txtDescuento = new javax.swing.JTextField(descStr);

        panelEdicion.add(new javax.swing.JLabel("Cantidad:"));
        panelEdicion.add(txtCantidad);
        panelEdicion.add(new javax.swing.JLabel("Precio Unitario (€):"));
        panelEdicion.add(txtPrecio);
        panelEdicion.add(new javax.swing.JLabel("Descuento (%):"));
        panelEdicion.add(txtDescuento);

        // 3. Mostrar el diálogo
        int resultado = JOptionPane.showConfirmDialog(this, panelEdicion,
                "Editar " + descProducto, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // 4. Parsear nuevos valores (Reemplazamos coma por punto por seguridad)
                BigDecimal nuevaCant = new BigDecimal(txtCantidad.getText().replace(",", "."));
                BigDecimal nuevoPrecio = new BigDecimal(txtPrecio.getText().replace(",", "."));
                BigDecimal nuevoDesc = new BigDecimal(txtDescuento.getText().replace(",", "."));
                BigDecimal ivaPercent = new BigDecimal(ivaStr.replace(",", "."));

                // 5. Recalcular Total de la Línea (Lógica PDF Fiskaly)
                // A. Descuento Unitario
                BigDecimal descuentoUnitario = nuevoPrecio.multiply(nuevoDesc)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                // B. Precio Neto
                BigDecimal precioNetoUnitario = nuevoPrecio.subtract(descuentoUnitario);

                // C. Base Imponible
                BigDecimal baseImponible = precioNetoUnitario.multiply(nuevaCant);

                // D. IVA
                BigDecimal montoIVA = baseImponible.multiply(ivaPercent)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                // E. Total
                BigDecimal totalLinea = baseImponible.add(montoIVA);

                // 6. Actualizar la tabla
                model.setValueAt(nuevaCant, fila, 2);
                model.setValueAt(nuevoPrecio, fila, 3);
                model.setValueAt(nuevoDesc, fila, 4);
                model.setValueAt(totalLinea.setScale(2, RoundingMode.HALF_UP), fila, 6);

                // 7. Recalcular totales generales
                calcularTotalesFactura();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error en los números. Usa el punto '.' para decimales.");
            }
        }
    }

    private void calcularTotalesFactura() {
        DefaultTableModel modelLineas = (DefaultTableModel) tblInvoices.getModel();
        DefaultTableModel modelTotales = (DefaultTableModel) jTableTotal.getModel();

        // Mapa para agrupar: Clave = % IVA, Valor = Array[Base, CuotaIVA]
        Map<BigDecimal, BigDecimal[]> desgloseIva = new TreeMap<>();

        BigDecimal granTotalBase = BigDecimal.ZERO;
        BigDecimal granTotalIva = BigDecimal.ZERO;
        BigDecimal granTotalPagar = BigDecimal.ZERO;

        // 1. RECORRER Y CALCULAR
        for (int i = 0; i < modelLineas.getRowCount(); i++) {
            try {
                BigDecimal cant = new BigDecimal(modelLineas.getValueAt(i, 2).toString().replace(",", "."));
                BigDecimal precio = new BigDecimal(modelLineas.getValueAt(i, 3).toString().replace(",", "."));
                BigDecimal desc = new BigDecimal(modelLineas.getValueAt(i, 4).toString().replace(",", "."));
                BigDecimal ivaPorc = new BigDecimal(modelLineas.getValueAt(i, 5).toString().replace(",", "."));

                // --- Lógica EXACTA para coincidir con PDF ---
                // Descuento unitario redondeado
                BigDecimal descuentoUnitario = precio.multiply(desc)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                // Precio neto unitario
                BigDecimal precioNetoUnitario = precio.subtract(descuentoUnitario);

                // Base de la línea
                BigDecimal baseLinea = precioNetoUnitario.multiply(cant);

                // IVA de la línea
                BigDecimal cuotaIvaLinea = baseLinea.multiply(ivaPorc)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                // Total línea
                BigDecimal totalLinea = baseLinea.add(cuotaIvaLinea);

                // **IMPORTANTE**: Actualizamos el total visual de la línea por si el recálculo varió un céntimo
                modelLineas.setValueAt(totalLinea.setScale(2, RoundingMode.HALF_UP), i, 6);

                if (!desgloseIva.containsKey(ivaPorc)) {
                    desgloseIva.put(ivaPorc, new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                }

                BigDecimal[] acumulado = desgloseIva.get(ivaPorc);
                acumulado[0] = acumulado[0].add(baseLinea);
                acumulado[1] = acumulado[1].add(cuotaIvaLinea);

            } catch (Exception e) {
                // Ignorar filas vacías o errores de parseo momentáneos
            }
        }

        // 2. LLENAR TABLA INFERIOR
        modelTotales.setRowCount(0);

        for (Map.Entry<BigDecimal, BigDecimal[]> entrada : desgloseIva.entrySet()) {
            BigDecimal tipoIva = entrada.getKey();
            BigDecimal baseGrupo = entrada.getValue()[0];
            BigDecimal cuotaGrupo = entrada.getValue()[1];
            BigDecimal totalGrupo = baseGrupo.add(cuotaGrupo);

            modelTotales.addRow(new Object[]{
                baseGrupo.setScale(2, RoundingMode.HALF_UP) + " €",
                tipoIva.setScale(0, RoundingMode.DOWN) + " %",
                cuotaGrupo.setScale(2, RoundingMode.HALF_UP) + " €",
                totalGrupo.setScale(2, RoundingMode.HALF_UP) + " €"
            });

            granTotalBase = granTotalBase.add(baseGrupo);
            granTotalIva = granTotalIva.add(cuotaGrupo);
            granTotalPagar = granTotalPagar.add(totalGrupo);
        }

        // Fila final de TOTAL
        if (desgloseIva.size() > 0) {
            modelTotales.addRow(new Object[]{
                "----------", "TOTAL", "----------",
                "<html><b>" + granTotalPagar.setScale(2, RoundingMode.HALF_UP) + " €</b></html>"
            });
        } else {
            modelTotales.addRow(new Object[]{"0.00 €", "0 %", "0.00 €", "0.00 €"});
        }

        // --- 3. AJUSTE DINÁMICO DE ALTURA (SOLUCIÓN AL SCROLL) ---
        // Calculamos la altura necesaria: (Filas * AltoFila) + AltoCabecera + Un poco de margen
        int altoFila = jTableTotal.getRowHeight(); // Debería ser 40 según tu configuración
        int numFilas = modelTotales.getRowCount();
        int altoCabecera = jTableTotal.getTableHeader().getPreferredSize().height;
        if (altoCabecera == 0) {
            altoCabecera = 30; // Fallback por si no se ha renderizado
        }
        // +5 pixels extra por si acaso los bordes
        int alturaTotal = (numFilas * altoFila) + altoCabecera + 5;

        // Obtenemos el ancho actual para no cambiarlo, o usamos un mínimo
        int anchoActual = jScrollPaneTotalFactura.getPreferredSize().width;
        if (anchoActual < 100) {
            anchoActual = 100;
        }

        // Aplicamos la nueva dimensión al JScrollPane (contenedor)
        Dimension nuevaDimension = new Dimension(anchoActual, alturaTotal);
        jScrollPaneTotalFactura.setPreferredSize(nuevaDimension);
        jScrollPaneTotalFactura.setMinimumSize(nuevaDimension);

        // IMPORTANTE: Forzar al panel padre a repintar el diseño (GridBagLayout)
        this.revalidate();
        this.repaint();
    }

    private void eliminarLineaSeleccionada() {
        DefaultTableModel model = (DefaultTableModel) tblInvoices.getModel();
        int fila = tblInvoices.getSelectedRow();

        if (fila >= 0) {
            model.removeRow(fila);
            calcularTotalesFactura(); // Recalcular al borrar
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una línea para borrar.");
        }
    }

    // --- MÉTODO AUXILIAR: CALCULAR SERIE CON AÑO (Ej: "F26") ---
    private String obtenerSerieConAnio() {
        // 1. Determinar letra (F normal o R rectificativa)
        String letra = "F";
        if (this.tipoFactura != null
                && (this.tipoFactura.equalsIgnoreCase("CORRECTING") || this.tipoFactura.equalsIgnoreCase("RECTIFICATIVA"))) {
            letra = "R";
        }

        // 2. Obtener año actual (2 dígitos)
        // java.time.Year.now().getValue() devuelve 2026 -> substring(2) coge "26"
        String anio = String.valueOf(java.time.Year.now().getValue()).substring(2);

        // 3. Retornar concatenado
        return letra + anio;
    }

    //---- MÉTODOS RELACIONADOS CON JSON FISKALY -----
    private Rate obtenerRateSegunIva(BigDecimal ivaTabla) {
        // Convertimos a entero para comparar fácil (21.00 -> 21)
        int valor = ivaTabla.intValue();

        switch (valor) {
            case 21:
                return Rate.IVA_21;
            case 10:
                return Rate.IVA_10;
            case 4:
                return Rate.IVA_4;
            case 0:
                return Rate.IVA_0; // Tu constructor convertirá esto mágicamente a EXEMPT_1
            default:
                // Por defecto, si hay un tipo raro, podrías lanzar error o asumir 21
                System.err.println("IVA no reconocido: " + valor + ". Se asigna IVA_21 por defecto.");
                return Rate.IVA_21;
        }
    }
}

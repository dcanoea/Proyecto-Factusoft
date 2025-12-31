/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.interfaz;

/**
 *
 * @author DavidCe
 */
public class PanelClientes extends javax.swing.JPanel {

    /**
     * Creates new form PanelClientes
     */
    public PanelClientes() {
        initComponents();

        // --- 1. TABLA
        // --- APLICAR ESTILO A LA TABLA ---
        Estilos.configurarTabla(tblClients, jScrollPaneCenter);

        // --- DEFINIR ANCHOS DE COLUMNAS ---
        // Nº Cliente: 90px (Tamaño ideal para números de 4-5 cifras)
        tblClients.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblClients.getColumnModel().getColumn(0).setMaxWidth(110);

        // NIF: 110px (Cabe perfecto "12345678A")
        tblClients.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblClients.getColumnModel().getColumn(1).setMaxWidth(130);

        // NOMBRE: 250px (Le quitamos sitio para dárselo al Email)
        // Al no ponerle MaxWidth, si maximizas la pantalla, esta columna crecerá para ocupar el hueco
        tblClients.getColumnModel().getColumn(2).setPreferredWidth(250);

        // EMAIL: 220px (Ahora es mucho más ancho para correos largos)
        tblClients.getColumnModel().getColumn(3).setPreferredWidth(220);

        // TELÉFONO: 140px (Asegurado para caber "+34123456789")
        tblClients.getColumnModel().getColumn(4).setPreferredWidth(140);
        tblClients.getColumnModel().getColumn(4).setMaxWidth(160);

        // Bloquear reordenamiento
        tblClients.getTableHeader().setReorderingAllowed(false);

        // --- 2. BARRA DE BÚSQUEDA
        // --- APLICAR ESTILO A LA BARRA DE BÚSQUEDA ---
        Estilos.configurarBarraBusqueda(txtSearch);
        txtSearch.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Buscar Cliente por Nombre o DNI/NIE/CIF");

        // --- 3. BOTONES
        // --- APLICAR ESTILO A LOS BOTONES ---
        // Creamos una lista con todos los botones para aplicarles el estilo "Píldora Negra" de golpe
        javax.swing.JButton[] botonesAccion = {
            btnAddClient, // Añadir Cliente
            btnInvoices, // Ver facturas
            btnClientDetails, // Datos
            btnEditClient, // Editar
            btnDeleteClient // Borrar
        };

        for (javax.swing.JButton btn : botonesAccion) {
            Estilos.configurarBotonAccion(btn);
        }

        // Botón Home como Icono
        com.formdev.flatlaf.extras.FlatSVGIcon homeIcon = new com.formdev.flatlaf.extras.FlatSVGIcon("img/home_Icon.svg", 32, 32);
        // Usamos Color.BLACK (o Color.decode("#111111") para negro suave)
        homeIcon.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(color -> java.awt.Color.BLACK));
        btnHome.setIcon(homeIcon);
        btnHome.setText("");
        // Estilo "invisible" para que solo se vea el icono
        btnHome.setContentAreaFilled(false);
        btnHome.setBorderPainted(false);
        btnHome.setFocusPainted(false);
        btnHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Añadir icono a los botones
        setIconoBlanco(btnInvoices, "img/invoice_Icon.svg");
        setIconoBlanco(btnClientDetails, "img/details_icon.svg");
        setIconoBlanco(btnEditClient, "img/edit_icon.svg");
        setIconoBlanco(btnDeleteClient, "img/delete_icon.svg");

        // --- CONFIGURACIÓN BOTÓN AÑADIR CLIENTE ---
        // 1. Cargar el icono SVG (Tamaño 20x20)
        com.formdev.flatlaf.extras.FlatSVGIcon iconAdd = new com.formdev.flatlaf.extras.FlatSVGIcon("img/add_Icon.svg", 20, 20);

        // 2. Pintarlo de BLANCO (porque el botón es negro)
        iconAdd.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(c -> java.awt.Color.WHITE));

        // 3. Asignarlo al botón
        btnAddClient.setIcon(iconAdd);

        // 4. Ajustes visuales
        btnAddClient.setIconTextGap(10); // Espacio entre el (+) y el texto
        btnAddClient.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT); // Texto a la derecha del icono

        // Asegurarnos de que el texto esté centrado verticalmente
        btnAddClient.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);

        // Ajustes finales de texto y márgenes
        javax.swing.JButton[] botones = {btnInvoices, btnClientDetails, btnEditClient, btnDeleteClient};
        for (javax.swing.JButton btn : botones) {
            btn.setIconTextGap(15);
            btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            // Ajusta este margen si quieres los botones más o menos anchos
            btn.setMargin(new java.awt.Insets(10, 15, 10, 15));
        }
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
        txtSearch = new javax.swing.JTextField();
        btnAddClient = new javax.swing.JButton();
        jScrollPaneCenter = new javax.swing.JScrollPane();
        tblClients = new javax.swing.JTable();
        jPanelRight = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        btnInvoices = new javax.swing.JButton();
        btnClientDetails = new javax.swing.JButton();
        btnEditClient = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        btnDeleteClient = new javax.swing.JButton();

        setBackground(new java.awt.Color(160, 238, 204));
        setLayout(new java.awt.BorderLayout());

        jPanelCenter.setBackground(new java.awt.Color(160, 238, 204));
        jPanelCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelCenter.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 15, 10);
        jPanelCenter.add(txtSearch, gridBagConstraints);

        btnAddClient.setText("Añadir Cliente");
        btnAddClient.addActionListener(this::btnAddClientActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 15, 10);
        jPanelCenter.add(btnAddClient, gridBagConstraints);

        jScrollPaneCenter.setOpaque(false);

        tblClients.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nº Cliente", "NIF/NIE/CIF", "Nombre", "Email", "Teléfono"
            }
        ));
        jScrollPaneCenter.setViewportView(tblClients);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelCenter.add(jScrollPaneCenter, gridBagConstraints);

        add(jPanelCenter, java.awt.BorderLayout.CENTER);

        jPanelRight.setOpaque(false);
        jPanelRight.setPreferredSize(new java.awt.Dimension(200, 0));
        jPanelRight.setLayout(new java.awt.GridBagLayout());

        btnHome.setText("HOME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnHome, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        jPanelRight.add(filler1, gridBagConstraints);

        btnInvoices.setText("Ver Facturas");
        btnInvoices.addActionListener(this::btnInvoicesActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnInvoices, gridBagConstraints);

        btnClientDetails.setText("Datos Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnClientDetails, gridBagConstraints);

        btnEditClient.setText("Editar Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnEditClient, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        jPanelRight.add(filler2, gridBagConstraints);

        btnDeleteClient.setText("Borrar Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 10);
        jPanelRight.add(btnDeleteClient, gridBagConstraints);

        add(jPanelRight, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void btnInvoicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvoicesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInvoicesActionPerformed

    private void btnAddClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddClientActionPerformed
        // 1. Instanciar el formulario de creación
        PanelCrearCliente pnlCrear = new PanelCrearCliente();

        // 2. Obtener el contenedor padre
        javax.swing.JPanel parent = (javax.swing.JPanel) getParent();

        // --- PASO CLAVE: CAPTURAR EL COMPORTAMIENTO ACTUAL ---
        // Guardamos los "ActionListeners" que tiene el botón Home actual antes de desaparecer.
        // Esto captura la lógica que le dio la ventana Principal.
        java.awt.event.ActionListener[] listenersHome = btnHome.getActionListeners();

        // 3. Configurar el botón "VOLVER" del formulario nuevo
        pnlCrear.getBtnBack().addActionListener(e -> {
            // Al volver, limpiamos el panel derecho
            parent.removeAll();

            // Creamos el nuevo panel
            PanelClientes nuevoPanel = new PanelClientes();

            // --- PASO CLAVE: RESTAURAR EL COMPORTAMIENTO ---
            // Le "pegamos" los listeners guardados al botón del nuevo panel
            for (java.awt.event.ActionListener al : listenersHome) {
                nuevoPanel.getBtnHome().addActionListener(al);
            }

            parent.add(nuevoPanel, java.awt.BorderLayout.CENTER);
            parent.revalidate();
            parent.repaint();
        });

        // 5. CAMBIAZO: Quitar lista (this), poner formulario (pnlCrear)
        parent.removeAll();
        parent.add(pnlCrear, java.awt.BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }//GEN-LAST:event_btnAddClientActionPerformed

    private void setIconoBlanco(javax.swing.JButton btn, String rutaSvg) {
        com.formdev.flatlaf.extras.FlatSVGIcon icon = new com.formdev.flatlaf.extras.FlatSVGIcon(rutaSvg, 20, 20);
        // ESTO ES LA MAGIA: Fuerza el color blanco
        icon.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(color -> java.awt.Color.WHITE));
        btn.setIcon(icon);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddClient;
    private javax.swing.JButton btnClientDetails;
    private javax.swing.JButton btnDeleteClient;
    private javax.swing.JButton btnEditClient;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnInvoices;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JScrollPane jScrollPaneCenter;
    private javax.swing.JTable tblClients;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    // Método puente para acceder al botón Home desde fuera
    public javax.swing.JButton getBtnHome() {
        return btnHome;
    }
}

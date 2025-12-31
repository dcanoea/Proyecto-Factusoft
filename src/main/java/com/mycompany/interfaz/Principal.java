/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.interfaz;

/**
 *
 * @author David CE
 */
public class Principal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Principal.class.getName());
    private javax.swing.JLabel lblLogoEmpresa = new javax.swing.JLabel();

    /**
     * Creates new form Login
     */
    public Principal() {
        initComponents();
        // Maximizar
        this.setExtendedState(MAXIMIZED_BOTH);

        // --- Aplicar el estilo a los botones ---
        Estilos.configurarBotonMenu(btnClients, "CLIENTES", "img/clients_Icon.svg");
        Estilos.configurarBotonMenu(btnProducts, "PRODUCTOS", "img/products_Icon.svg");
        Estilos.configurarBotonMenu(btnInvoices, "FACTURAS", "img/invoice_Icon.svg");
        Estilos.configurarBotonMenu(btnConfig, "CONFIGURACIÓN", "img/configuration_Icon.svg");

    }

    /**
     * Este método convierte la pantalla de INICIO en la pantalla de TRABAJO
     * (Menú estrecho)
     */
    public void activarModoMinimizado() {
        // 1. AJUSTES DEL PANEL
        jPanelLeft.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelLeft.setPreferredSize(null);

        java.awt.GridBagLayout layoutPrincipal = (java.awt.GridBagLayout) getContentPane().getLayout();
        java.awt.GridBagConstraints gbcLeft = layoutPrincipal.getConstraints(jPanelLeft);
        java.awt.GridBagConstraints gbcRight = layoutPrincipal.getConstraints(jPanelRight);

        gbcLeft.weightx = 0.15;
        gbcRight.weightx = 0.85;

        layoutPrincipal.setConstraints(jPanelLeft, gbcLeft);
        layoutPrincipal.setConstraints(jPanelRight, gbcRight);

        // 2. LIMPIEZA
        jPanelLeft.removeAll();
        jPanelLeft.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // -------------------------------------------------------------------------
        // A) LOGO EMPRESA (fiskaly) - ¡AHORA MÁS GRANDE!
        // -------------------------------------------------------------------------
        lblLogoEmpresa.setText("");
        lblLogoEmpresa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        try {
            // 1. Cargar imagen
            java.net.URL imgUrl = getClass().getResource("/img/fiskaly.png");
            if (imgUrl == null) {
                imgUrl = getClass().getResource("/main/resources/img/fiskaly.png");
            }

            if (imgUrl != null) {
                javax.swing.ImageIcon iconoOriginal = new javax.swing.ImageIcon(imgUrl);

                // --- AQUÍ ESTÁ EL CAMBIO ---
                // Antes: 150 (Muy pequeño) -> AHORA: 230 (Ocupará casi todo el ancho del menú)
                java.awt.Image imgEscalada = iconoOriginal.getImage().getScaledInstance(230, -1, java.awt.Image.SCALE_SMOOTH);

                lblLogoEmpresa.setIcon(new javax.swing.ImageIcon(imgEscalada));
            } else {
                lblLogoEmpresa.setText("fiskaly");
            }
        } catch (Exception e) {
            lblLogoEmpresa.setText("Error Logo");
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new java.awt.Insets(40, 0, 40, 0); // Margen generoso arriba y abajo
        jPanelLeft.add(lblLogoEmpresa, gbc);

        // -------------------------------------------------------------------------
        // B) BOTONES
        // -------------------------------------------------------------------------
        Estilos.configurarBotonMenu(btnClients, "CLIENTES", "img/clients_Icon.svg");
        Estilos.configurarBotonMenu(btnProducts, "PRODUCTOS", "img/products_Icon.svg");
        Estilos.configurarBotonMenu(btnInvoices, "FACTURAS", "img/invoice_Icon.svg");
        Estilos.configurarBotonMenu(btnConfig, "CONFIGURACIÓN", "img/configuration_Icon.svg");

        // Botón Activo (Negro)
        btnClients.setBackground(java.awt.Color.decode("#111111"));
        btnClients.setForeground(java.awt.Color.WHITE);
        try {
            com.formdev.flatlaf.extras.FlatSVGIcon iconClient = new com.formdev.flatlaf.extras.FlatSVGIcon("img/clients_Icon.svg", 50, 50);
            iconClient.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(c -> java.awt.Color.WHITE));
            btnClients.setIcon(iconClient);
        } catch (Exception e) {
        }

        gbc.weighty = 0.0;
        gbc.insets = new java.awt.Insets(5, 15, 5, 15);

        gbc.gridy = 1;
        jPanelLeft.add(btnClients, gbc);
        gbc.gridy = 2;
        jPanelLeft.add(btnProducts, gbc);
        gbc.gridy = 3;
        jPanelLeft.add(btnInvoices, gbc);
        gbc.gridy = 4;
        jPanelLeft.add(btnConfig, gbc);

        // -------------------------------------------------------------------------
        // C) EMPUJADOR (Espacio vacío)
        // -------------------------------------------------------------------------
        javax.swing.Box.Filler empujador = new javax.swing.Box.Filler(
                new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        jPanelLeft.add(empujador, gbc);

        // -------------------------------------------------------------------------
        // D) LOGO FACTUSOFT (ABAJO) - CON BORDE AJUSTADO
        // -------------------------------------------------------------------------
        try {
            javax.swing.ImageIcon logoFactu = new javax.swing.ImageIcon(getClass().getResource("/img/LOGO.png"));
            // Ajustamos a 150px para que con el borde no quede apretado
            java.awt.Image imgF = logoFactu.getImage().getScaledInstance(150, -1, java.awt.Image.SCALE_SMOOTH);

            lblFactuSoftIcon.setIcon(new javax.swing.ImageIcon(imgF));
            lblFactuSoftIcon.setText("");

            // Borde: Línea negra fina + 0px de espacio en blanco interno
            lblFactuSoftIcon.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 1),
                    javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));

        } catch (Exception e) {
            lblFactuSoftIcon.setText("FactuSoft");
        }
        lblFactuSoftIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // --- CONFIGURACIÓN DE POSICIÓN ---
        gbc.gridy = 6;
        gbc.weighty = 0.0;

        // IMPORTANTE: Esto evita que el borde se estire a lo ancho del panel
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.CENTER; // Lo centramos

        gbc.insets = new java.awt.Insets(0, 0, 30, 0); // Margen inferior
        jPanelLeft.add(lblFactuSoftIcon, gbc);

        // -------------------------------------------------------------------------
        // FINALIZAR ACTUALIZACIÓN
        // -------------------------------------------------------------------------
        jPanelLeft.revalidate();
        jPanelLeft.repaint();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /**
     * Restaura la pantalla de INICIO clonando exactamente el diseño original.
     */
    public void restaurarModoInicio() {
        // 1. RESTAURAR PESOS ORIGINALES (35% Izquierda - 65% Derecha)
        java.awt.GridBagLayout layoutPrincipal = (java.awt.GridBagLayout) getContentPane().getLayout();
        java.awt.GridBagConstraints gbcLeft = layoutPrincipal.getConstraints(jPanelLeft);
        java.awt.GridBagConstraints gbcRight = layoutPrincipal.getConstraints(jPanelRight);

        gbcLeft.weightx = 0.35;
        gbcRight.weightx = 0.65;

        layoutPrincipal.setConstraints(jPanelLeft, gbcLeft);
        layoutPrincipal.setConstraints(jPanelRight, gbcRight);

        // 2. LIMPIEZA
        jPanelLeft.removeAll();
        jPanelRight.removeAll();

        // =========================================================
        // 3. RECONSTRUCCIÓN PANEL IZQUIERDO
        // =========================================================
        jPanelLeft.setLayout(new java.awt.GridBagLayout());

        // --- A) LOGO FACTUSOFT ---
        lblFactuSoftIcon.setText("");
        lblFactuSoftIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        try {
            javax.swing.ImageIcon logo = new javax.swing.ImageIcon(getClass().getResource("/img/LOGO_small.png"));
            lblFactuSoftIcon.setIcon(logo);
            // BORDE NEGRO ORIGINAL
            lblFactuSoftIcon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        } catch (Exception e) {
            lblFactuSoftIcon.setText("FactuSoft");
        }

        // Constraints Logo (NO ESTIRAR, CENTRADO)
        java.awt.GridBagConstraints gbcLogo = new java.awt.GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.fill = java.awt.GridBagConstraints.NONE;
        gbcLogo.anchor = java.awt.GridBagConstraints.CENTER;
        gbcLogo.insets = new java.awt.Insets(50, 0, 50, 0);
        jPanelLeft.add(lblFactuSoftIcon, gbcLogo);

        // --- B) BOTONES ---
        // Restauramos usando tus Estilos originales
        restaurarBotonOriginal(btnClients, "CLIENTES", "img/clients_Icon.svg");
        restaurarBotonOriginal(btnProducts, "PRODUCTOS", "img/products_Icon.svg");
        restaurarBotonOriginal(btnInvoices, "FACTURAS", "img/invoice_Icon.svg");
        restaurarBotonOriginal(btnConfig, "CONFIGURACIÓN", "img/configuration_Icon.svg");

        // Constraints Botones (ESTIRAR)
        java.awt.GridBagConstraints gbcBtn = new java.awt.GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.fill = java.awt.GridBagConstraints.BOTH;
        gbcBtn.weightx = 1.0;
        gbcBtn.insets = new java.awt.Insets(15, 20, 15, 20); // Márgenes originales

        gbcBtn.gridy = 1;
        jPanelLeft.add(btnClients, gbcBtn);
        gbcBtn.gridy = 2;
        jPanelLeft.add(btnProducts, gbcBtn);
        gbcBtn.gridy = 3;
        jPanelLeft.add(btnInvoices, gbcBtn);
        gbcBtn.gridy = 4;
        jPanelLeft.add(btnConfig, gbcBtn);

        // Empujador
        java.awt.GridBagConstraints gbcFiller = new java.awt.GridBagConstraints();
        gbcFiller.gridx = 0;
        gbcFiller.gridy = 5;
        gbcFiller.weighty = 1.0;
        jPanelLeft.add(new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767)), gbcFiller);

        // =========================================================
        // 4. RECONSTRUCCIÓN PANEL DERECHO
        // =========================================================
        jPanelRight.setLayout(new java.awt.GridBagLayout());

        // Configuración ROBUSTA para centrar el logo pase lo que pase
        lblEnterpriseIcon.setText("");
        lblEnterpriseIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // <--- ESTO CENTRA LA IMAGEN DENTRO DE LA ETIQUETA
        lblEnterpriseIcon.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        try {
            javax.swing.ImageIcon logoGrande = new javax.swing.ImageIcon(getClass().getResource("/img/fiskaly.png"));
            lblEnterpriseIcon.setIcon(logoGrande);
        } catch (Exception e) {
            lblEnterpriseIcon.setText("fiskaly");
        }

        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.fill = java.awt.GridBagConstraints.BOTH; // Que la etiqueta ocupe todo el panel
        gbcRight.weightx = 1.0; // Importante para que 'fill' funcione
        gbcRight.weighty = 1.0;
        jPanelRight.add(lblEnterpriseIcon, gbcRight);

        // 5. REFRESCO
        jPanelLeft.revalidate();
        jPanelLeft.repaint();
        jPanelRight.revalidate();
        jPanelRight.repaint();
    }

    /**
     * Método centralizado para gestionar el restaurado de los botones del menú.
     *
     * @param btnSeleccionado El botón que acabamos de pulsar.
     * @param rutaIcono La ruta del icono SVG de ese botón (para pintarlo de
     * blanco).
     */
    // --- Helper corregido: Usa Estilos y restaura tamaño ---
    private void restaurarBotonOriginal(javax.swing.JButton btn, String texto, String rutaIcono) {
        // 1. Limpiar estilos "extraños" que pudieran haber quedado del modo minimizado
        btn.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, null);

        // 2. Aplicar TU estilo original (Esto pone la alineación a la izquierda, colores, etc.)
        Estilos.configurarBotonMenu(btn, texto, rutaIcono);

        // 3. Restaurar el tamaño mínimo que se pierde al cambiar de panel
        // (Esto es lo que faltaba para que se vieran "gorditos" como al principio)
        btn.setMinimumSize(new java.awt.Dimension(100, 50));
        btn.setPreferredSize(null); // Dejamos que el layout decida el tamaño final
    }

    /**
     * Método centralizado para gestionar el resaltado de los botones del menú.
     *
     * @param btnSeleccionado El botón que acabamos de pulsar.
     * @param rutaIcono La ruta del icono SVG de ese botón (para pintarlo de
     * blanco).
     */
    private void seleccionarBoton(javax.swing.JButton btnSeleccionado, String rutaIcono) {
        // --- 1. RESETEAR TODOS A ESTADO INACTIVO (Verde / Negro) ---
        // Array con todos los botones y sus iconos originales
        javax.swing.JButton[] todos = {btnClients, btnProducts, btnInvoices, btnConfig};
        String[] iconos = {"img/clients_Icon.svg", "img/products_Icon.svg", "img/invoice_Icon.svg", "img/configuration_Icon.svg"};

        for (int i = 0; i < todos.length; i++) {
            javax.swing.JButton btn = todos[i];
            // Restaurar color fondo y texto
            btn.setBackground(Estilos.COLOR_FONDO_MENTA); // Menta
            btn.setForeground(java.awt.Color.BLACK);

            // Restaurar icono negro original
            try {
                // Usamos 50x50 porque estamos en modo menú lateral (ajusta si usas otro tamaño)
                btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(iconos[i], 50, 50));
            } catch (Exception e) {
            }
        }

        // --- 2. RESALTAR EL SELECCIONADO (Negro / Blanco) ---
        if (btnSeleccionado != null) {
            btnSeleccionado.setBackground(java.awt.Color.decode("#111111")); // Negro
            btnSeleccionado.setForeground(java.awt.Color.WHITE);             // Blanco

            // Poner el icono específico en BLANCO
            try {
                com.formdev.flatlaf.extras.FlatSVGIcon iconWhite = new com.formdev.flatlaf.extras.FlatSVGIcon(rutaIcono, 50, 50);
                iconWhite.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(c -> java.awt.Color.WHITE));
                btnSeleccionado.setIcon(iconWhite);
            } catch (Exception e) {
            }
        }
    }

    public void abrirNuevaFactura(String cliente, String tipo) {
        // 1. Crear el nuevo panel pasándole los datos
        PanelCrearFactura nuevoPanel = new PanelCrearFactura(cliente, tipo);

        // 2. Ajustar tamaño (importante para que no salga enano)
        nuevoPanel.setSize(jPanelRight.getWidth(), jPanelRight.getHeight());
        nuevoPanel.setLocation(0, 0);

        // 3. Reemplazar
        jPanelRight.removeAll();
        jPanelRight.add(nuevoPanel, java.awt.BorderLayout.CENTER);
        jPanelRight.revalidate();
        jPanelRight.repaint();
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

        jPanelLeft = new javax.swing.JPanel();
        lblFactuSoftIcon = new javax.swing.JLabel();
        btnClients = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnInvoices = new javax.swing.JButton();
        btnConfig = new javax.swing.JButton();
        jPanelRight = new javax.swing.JPanel();
        lblEnterpriseIcon = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(640, 360));
        setSize(new java.awt.Dimension(1280, 720));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelLeft.setBackground(new java.awt.Color(160, 238, 204));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelLeft.setLayout(new java.awt.GridBagLayout());

        lblFactuSoftIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/LOGO_small.png"))); // NOI18N
        lblFactuSoftIcon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(50, 0, 50, 0);
        jPanelLeft.add(lblFactuSoftIcon, gridBagConstraints);

        btnClients.setMinimumSize(new java.awt.Dimension(100, 50));
        btnClients.addActionListener(this::btnClientsActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 20, 15, 20);
        jPanelLeft.add(btnClients, gridBagConstraints);

        btnProducts.setMinimumSize(new java.awt.Dimension(100, 50));
        btnProducts.addActionListener(this::btnProductsActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 20, 15, 20);
        jPanelLeft.add(btnProducts, gridBagConstraints);

        btnInvoices.setMinimumSize(new java.awt.Dimension(100, 50));
        btnInvoices.addActionListener(this::btnInvoicesActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 20, 15, 20);
        jPanelLeft.add(btnInvoices, gridBagConstraints);

        btnConfig.setMinimumSize(new java.awt.Dimension(100, 50));
        btnConfig.addActionListener(this::btnConfigActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 20, 15, 20);
        jPanelLeft.add(btnConfig, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelLeft, gridBagConstraints);

        jPanelRight.setBackground(new java.awt.Color(160, 238, 204));
        jPanelRight.setPreferredSize(new java.awt.Dimension(900, 600));
        jPanelRight.setLayout(new java.awt.GridBagLayout());

        lblEnterpriseIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fiskaly.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelRight.add(lblEnterpriseIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.65;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelRight, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientsActionPerformed
        activarModoMinimizado();

        // 1. GESTIÓN VISUAL (Una sola línea)
        seleccionarBoton(btnClients, "img/clients_Icon.svg");

        // 2. CARGAR PANEL
        PanelClientes pnl = new PanelClientes();
        pnl.setSize(1000, 800);
        pnl.setLocation(0, 0);

        pnl.getBtnHome().addActionListener(e -> restaurarModoInicio());

        jPanelRight.removeAll();
        jPanelRight.setLayout(new java.awt.BorderLayout());
        jPanelRight.add(pnl, java.awt.BorderLayout.CENTER);
        jPanelRight.revalidate();
        jPanelRight.repaint();
    }//GEN-LAST:event_btnClientsActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        activarModoMinimizado();

        // 1. GESTIÓN VISUAL (Una sola línea)
        seleccionarBoton(btnProducts, "img/products_Icon.svg");

        // 2. CARGAR PANEL
        PanelProductos pnl = new PanelProductos();
        pnl.setSize(1000, 800);
        pnl.setLocation(0, 0);

        pnl.getBtnHome().addActionListener(e -> restaurarModoInicio());

        jPanelRight.removeAll();
        jPanelRight.setLayout(new java.awt.BorderLayout());
        jPanelRight.add(pnl, java.awt.BorderLayout.CENTER);
        jPanelRight.revalidate();
        jPanelRight.repaint();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnInvoicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvoicesActionPerformed
        activarModoMinimizado();

        // 1. GESTIÓN VISUAL
        seleccionarBoton(btnInvoices, "img/invoice_Icon.svg");

        // 2. CARGAR PANEL
        PanelFacturas pnl = new PanelFacturas();
        pnl.setSize(1000, 800);
        pnl.setLocation(0, 0);

        pnl.getBtnHome().addActionListener(e -> restaurarModoInicio());

        jPanelRight.removeAll();
        jPanelRight.setLayout(new java.awt.BorderLayout());
        jPanelRight.add(pnl, java.awt.BorderLayout.CENTER);
        jPanelRight.revalidate();
        jPanelRight.repaint();
    }//GEN-LAST:event_btnInvoicesActionPerformed

    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        activarModoMinimizado();

        // 1. GESTIÓN VISUAL
        seleccionarBoton(btnConfig, "img/configuration_Icon.svg");

        // 2. CARGAR PANEL
        PanelConfig pnl = new PanelConfig();
        pnl.setSize(1000, 800);
        pnl.setLocation(0, 0);

        pnl.getBtnHome().addActionListener(e -> restaurarModoInicio());

        jPanelRight.removeAll();
        jPanelRight.setLayout(new java.awt.BorderLayout());
        jPanelRight.add(pnl, java.awt.BorderLayout.CENTER);
        jPanelRight.revalidate();
        jPanelRight.repaint();
    }//GEN-LAST:event_btnConfigActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        // Instalamos el tema FlatLaf Light (o Dark)
        try {
            // Puedes elegir: FlatLightLaf.setup() o FlatDarkLaf.setup()
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Principal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClients;
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnInvoices;
    private javax.swing.JButton btnProducts;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JLabel lblEnterpriseIcon;
    private javax.swing.JLabel lblFactuSoftIcon;
    // End of variables declaration//GEN-END:variables
}

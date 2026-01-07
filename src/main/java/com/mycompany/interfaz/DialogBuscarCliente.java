package com.mycompany.interfaz;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
import com.formdev.flatlaf.extras.FlatSVGIcon;

/**
 *
 * @author poker
 */
public class DialogBuscarCliente extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DialogBuscarCliente.class.getName());

    // 1. Instanciamos el DAO para poder buscar
    private com.mycompany.dao.ClienteDAO clienteDAO = new com.mycompany.dao.ClienteDAO();

    // 2. Variable para guardar el cliente que el usuario elija
    private com.mycompany.dominio.Cliente clienteSeleccionado;

    /**
     * Creates new form DialogCrearFactura
     */
    public DialogBuscarCliente(java.awt.Frame parent, boolean modal) {
        super(parent, modal ? java.awt.Dialog.ModalityType.APPLICATION_MODAL : java.awt.Dialog.ModalityType.MODELESS);
        initComponents();
        // 1. APLICAR ESTILOS VISUALES
        configurarEstilos();

        // 2. CONFIGURAR COLUMNAS DE LA TABLA
        configurarColumnas();

        // 3. AJUSTAR TAMAÑO Y POSICIÓN
        pack();
        setLocationRelativeTo(parent);
    }

// --- MÉTODOS PROPIOS (COPIAR Y PEGAR EN SOURCE) ---
    private void configurarColumnas() {
        // 1. DEFINIR EL MODELO POR CÓDIGO (Sobreescribe a Matisse)
        // Creamos 6 columnas: ID (0), Nombre (1), Apellido1 (2), Apellido2 (3), DNI (4), Comercial (5)
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre", "Apellido 1", "Apellido 2", "DNI/NIE/CIF", "Nombre Comercial"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        tblResultados.setModel(modelo);

        // 2. OCULTAR LA COLUMNA ID (Índice 0)
        tblResultados.getColumnModel().getColumn(0).setMinWidth(0);
        tblResultados.getColumnModel().getColumn(0).setMaxWidth(0);
        tblResultados.getColumnModel().getColumn(0).setPreferredWidth(0);

        // 3. ANCHOS DE LAS VISIBLES
        tblResultados.getColumnModel().getColumn(1).setPreferredWidth(100); // Nombre
        tblResultados.getColumnModel().getColumn(2).setPreferredWidth(100); // Apellido 1
        tblResultados.getColumnModel().getColumn(3).setPreferredWidth(100); // Apellido 2
        tblResultados.getColumnModel().getColumn(4).setPreferredWidth(100); // DNI
        tblResultados.getColumnModel().getColumn(5).setPreferredWidth(200); // Comercial

        // Altura de filas cómoda
        tblResultados.setRowHeight(30);
    }

    private void configurarEstilos() {
        panelPrincipal.setBackground(Estilos.COLOR_FONDO_MENTA);

        Estilos.configurarBarraBusqueda(txtBuscar);
        txtBuscar.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Nombre, DNI o Comercial...");

        Estilos.configurarBotonAccion(btnBuscar);
        Estilos.configurarBotonAccion(btnAceptar);
        Estilos.configurarBotonAccion(btnCancelar);

        // Configura aquí tu icono si tienes la ruta correcta
        FlatSVGIcon iconSearch = new FlatSVGIcon("img/search.svg", 18, 18);
        iconSearch.setColorFilter(new FlatSVGIcon.ColorFilter(c -> java.awt.Color.WHITE));
        btnBuscar.setIcon(iconSearch);
        btnBuscar.setText("");

        Estilos.configurarTabla(tblResultados, panelCentral);
    }

    private void cargarClientes(String busqueda) {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tblResultados.getModel();
        modelo.setRowCount(0); // Limpiar siempre

        // OPTIMIZACIÓN: Si la búsqueda está vacía, NO hacemos nada (tabla vacía)
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return;
        }

        // Buscar en BBDD
        java.util.List<com.mycompany.dominio.Cliente> lista = clienteDAO.buscarPorNombre(busqueda);

        // Rellenar filas mapeando cada dato a su columna correcta
        for (com.mycompany.dominio.Cliente c : lista) {
            modelo.addRow(new Object[]{
                c.getId(), // 0: ID (Oculto)
                c.getFirstName(), // 1: Nombre
                c.getLastName1(), // 2: Apellido 1
                c.getLastName2(), // 3: Apellido 2
                c.getFiscalNumber(), // 4: DNI
                c.getCommercialName() // 5: Comercial
            });
        }
    }

    // Getter para recuperar el resultado desde fuera
    public com.mycompany.dominio.Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
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

        panelPrincipal = new javax.swing.JPanel();
        panelBusqueda = new javax.swing.JPanel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        panelCentral = new javax.swing.JScrollPane();
        tblResultados = new javax.swing.JTable();
        panelBotones = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        btnCancelar = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        btnAceptar = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nueva Factura");
        setModal(true);
        setResizable(false);

        panelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setLayout(new java.awt.BorderLayout());

        panelBusqueda.setOpaque(false);
        panelBusqueda.setLayout(new java.awt.GridBagLayout());

        txtBuscar.addActionListener(this::txtBuscarActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        panelBusqueda.add(txtBuscar, gridBagConstraints);

        btnBuscar.addActionListener(this::btnBuscarActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panelBusqueda.add(btnBuscar, gridBagConstraints);

        panelPrincipal.add(panelBusqueda, java.awt.BorderLayout.PAGE_START);

        panelCentral.setMinimumSize(new java.awt.Dimension(800, 300));
        panelCentral.setPreferredSize(new java.awt.Dimension(800, 300));

        tblResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Apellido 1", "Apellido 2", "DNI/NIE/CIF", "Nombre Comercial"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        panelCentral.setViewportView(tblResultados);

        panelPrincipal.add(panelCentral, java.awt.BorderLayout.CENTER);

        panelBotones.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new javax.swing.BoxLayout(panelBotones, javax.swing.BoxLayout.LINE_AXIS));
        panelBotones.add(filler4);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        panelBotones.add(btnCancelar);
        panelBotones.add(filler5);

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(this::btnAceptarActionPerformed);
        panelBotones.add(btnAceptar);
        panelBotones.add(filler3);

        panelPrincipal.add(panelBotones, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        cargarClientes(txtBuscar.getText());
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        int fila = tblResultados.getSelectedRow();

        if (fila == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecciona un cliente de la lista.");
            return;
        }

        // TRUCO: Recuperamos el ID de la columna 0 (que es invisible para el usuario pero existe en el modelo)
        int idCliente = (int) tblResultados.getValueAt(fila, 0);

        try {
            this.clienteSeleccionado = clienteDAO.obtenerPorId(idCliente);
            if (this.clienteSeleccionado == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al recuperar datos del cliente.");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.dispose(); // Cerramos ventana
        }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.clienteSeleccionado = null; // Nos aseguramos de limpiar la selección
        this.dispose();
}//GEN-LAST:event_btnCancelarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        cargarClientes(txtBuscar.getText());
    }//GEN-LAST:event_btnBuscarActionPerformed

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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DialogBuscarCliente dialog = new DialogBuscarCliente(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JScrollPane panelCentral;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTable tblResultados;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables

}

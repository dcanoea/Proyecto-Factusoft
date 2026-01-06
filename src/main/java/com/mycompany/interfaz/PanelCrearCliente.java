/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.interfaz;

import com.mycompany.dao.ClienteDAO;
import com.mycompany.dominio.Cliente;
import javax.swing.JOptionPane;

/**
 *
 * @author DavidCe
 */
public class PanelCrearCliente extends javax.swing.JPanel {

    private ClienteDAO clienteDAO = new ClienteDAO(); // Instancia del DAO
    private Cliente clienteEditar = null; // Variable para saber si editamos un cliente

    /**
     * Creates new form PanelClientes
     */
    // Constructor 1: MODO CREAR
    public PanelCrearCliente() {
        initComponents();

        // --- 1. APLICAR ESTILO A LOS CAMPOS DE TEXTO (CAMBIO: Usamos el nuevo estilo Formulario) ---
        javax.swing.JTextField[] campos = {
            txtAddress, txtCity, txtClientNumber, txtProvince, txtEmail,
            txtName, txtPhone1, txtPhone2, txtLastName1, txtLastName2, txtCommercialName, txtTaxNumber, txtZipCode
        };

        for (javax.swing.JTextField t : campos) {
            Estilos.configurarCampoFormulario(t);
        }

        // --- 2. APLICAR ESTILO A LAS ETIQUETAS (LABELS) ---
        // Esto hará que los textos "Nombre", "Dirección", etc. se vean GRANDES
        javax.swing.JLabel[] etiquetas = {
            lblAddress, lblCity, lblClientNumber, lblDistrict, lblEmail,
            lblName, lblPhone1, lblPhone2, lblLastName1, lblLastName2, lblTaxName, lblTaxNumber, lblZipCode
        };

        for (javax.swing.JLabel l : etiquetas) {
            Estilos.configurarEtiquetaFormulario(l);
        }

        // --- 3. BOTONES DE ACCIÓN ---
        setIconoBlanco(btnAddClient, "img/add_Icon.svg");
        setIconoBlanco(btnBack, "img/delete_Icon.svg");
        javax.swing.JButton[] botonesAccion = {btnAddClient, btnBack};

        for (javax.swing.JButton btn : botonesAccion) {
            Estilos.configurarBotonAccion(btn);
            // --- CORRECCIÓN DE ALINEACIÓN ---
            btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Texto Centrado
            btn.setIconTextGap(15); // Espacio entre icono y texto
            btn.setMargin(new java.awt.Insets(10, 20, 10, 20)); // Más gorditos
        }

        // --- CALCULAR Y MOSTRAR EL SIGUIENTE ID ---
        if (txtClientNumber != null) { // Asegúrate de que este es el nombre de tu variable
            try {
                int siguiente = clienteDAO.obtenerSiguienteId();
                txtClientNumber.setText(String.valueOf(siguiente));

                // Configuración visual
                txtClientNumber.setEditable(false); // No se puede escribir
                txtClientNumber.setFocusable(false); // El tabulador se salta este campo

                // Opcional: Centrar el número si te gusta más
                txtClientNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            } catch (Exception e) {
                txtClientNumber.setText("Auto"); // Fallback por si falla
            }
        }
    }

    // Constructor 2: MODO Editar
    public PanelCrearCliente(Cliente cliente) {
        initComponents();

        this.clienteEditar = cliente; // Guardamos el cliente que vamos a editar
        cargarDatosEdicion();         // Rellenamos los campos

        // --- 1. APLICAR ESTILO A LOS CAMPOS DE TEXTO (CAMBIO: Usamos el nuevo estilo Formulario) ---
        javax.swing.JTextField[] campos = {
            txtAddress, txtCity, txtClientNumber, txtProvince, txtEmail,
            txtName, txtPhone1, txtPhone2, txtLastName1, txtLastName2, txtCommercialName, txtTaxNumber, txtZipCode
        };

        for (javax.swing.JTextField t : campos) {
            Estilos.configurarCampoFormulario(t);
        }

        // --- 2. APLICAR ESTILO A LAS ETIQUETAS (LABELS) ---
        // Esto hará que los textos "Nombre", "Dirección", etc. se vean GRANDES
        javax.swing.JLabel[] etiquetas = {
            lblAddress, lblCity, lblClientNumber, lblDistrict, lblEmail,
            lblName, lblPhone1, lblPhone2, lblLastName1, lblLastName2, lblTaxName, lblTaxNumber, lblZipCode
        };

        for (javax.swing.JLabel l : etiquetas) {
            Estilos.configurarEtiquetaFormulario(l);
        }

        // --- 3. BOTONES DE ACCIÓN ---
        setIconoBlanco(btnAddClient, "img/add_Icon.svg");
        setIconoBlanco(btnBack, "img/delete_Icon.svg");
        javax.swing.JButton[] botonesAccion = {btnAddClient, btnBack};

        for (javax.swing.JButton btn : botonesAccion) {
            Estilos.configurarBotonAccion(btn);
            // --- CORRECCIÓN DE ALINEACIÓN ---
            btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Texto Centrado
            btn.setIconTextGap(15); // Espacio entre icono y texto
            btn.setMargin(new java.awt.Insets(10, 20, 10, 20)); // Más gorditos
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
        jPanelForm = new javax.swing.JPanel();
        lblClientNumber = new javax.swing.JLabel();
        lblTaxNumber = new javax.swing.JLabel();
        txtClientNumber = new javax.swing.JTextField();
        txtTaxNumber = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        lblTaxName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtCommercialName = new javax.swing.JTextField();
        lblLastName1 = new javax.swing.JLabel();
        lblLastName2 = new javax.swing.JLabel();
        txtLastName1 = new javax.swing.JTextField();
        txtLastName2 = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        lblZipCode = new javax.swing.JLabel();
        txtZipCode = new javax.swing.JTextField();
        lblCity = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        lblDistrict = new javax.swing.JLabel();
        txtProvince = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        lblPhone1 = new javax.swing.JLabel();
        txtPhone1 = new javax.swing.JTextField();
        lblPhone2 = new javax.swing.JLabel();
        txtPhone2 = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jPanelButtons = new javax.swing.JPanel();
        btnBack = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        btnAddClient = new javax.swing.JButton();
        jPanelRight = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setBackground(new java.awt.Color(160, 238, 204));
        setLayout(new java.awt.BorderLayout());

        jPanelCenter.setBackground(new java.awt.Color(160, 238, 204));
        jPanelCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanelCenter.setLayout(new java.awt.GridBagLayout());

        jPanelForm.setBackground(new java.awt.Color(203, 246, 227));
        jPanelForm.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(0, 80, 0, 80)));
        jPanelForm.setPreferredSize(new java.awt.Dimension(0, 0));
        jPanelForm.setLayout(new java.awt.GridBagLayout());

        lblClientNumber.setText("Nº Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 10, 500);
        jPanelForm.add(lblClientNumber, gridBagConstraints);

        lblTaxNumber.setText("NIF/NIE/CIF");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -298, 10, 100);
        jPanelForm.add(lblTaxNumber, gridBagConstraints);

        txtClientNumber.addActionListener(this::txtClientNumberActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 500);
        jPanelForm.add(txtClientNumber, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -300, 0, 100);
        jPanelForm.add(txtTaxNumber, gridBagConstraints);

        lblName.setText("Nombre");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 10, 400);
        jPanelForm.add(lblName, gridBagConstraints);

        lblTaxName.setText("Nombre Comercial");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, -298, 10, 10);
        jPanelForm.add(lblTaxName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 400);
        jPanelForm.add(txtName, gridBagConstraints);

        txtCommercialName.addActionListener(this::txtCommercialNameActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -300, 0, 10);
        jPanelForm.add(txtCommercialName, gridBagConstraints);

        lblLastName1.setText("Apellido 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 10, 300);
        jPanelForm.add(lblLastName1, gridBagConstraints);

        lblLastName2.setText("Apellido 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, -298, 10, 10);
        jPanelForm.add(lblLastName2, gridBagConstraints);

        txtLastName1.addActionListener(this::txtLastName1ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 400);
        jPanelForm.add(txtLastName1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -300, 0, 200);
        jPanelForm.add(txtLastName2, gridBagConstraints);

        lblAddress.setText("Dirección");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 10, 10);
        jPanelForm.add(lblAddress, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanelForm.add(txtAddress, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblZipCode.setText("Código Postal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 10, 500);
        jPanel1.add(lblZipCode, gridBagConstraints);

        txtZipCode.addActionListener(this::txtZipCodeActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 500);
        jPanel1.add(txtZipCode, gridBagConstraints);

        lblCity.setText("Ciudad");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, -448, 10, 200);
        jPanel1.add(lblCity, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -450, 0, 200);
        jPanel1.add(txtCity, gridBagConstraints);

        lblDistrict.setText("Provincia");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, -148, 10, 0);
        jPanel1.add(lblDistrict, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -150, 0, 0);
        jPanel1.add(txtProvince, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelForm.add(jPanel1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblPhone1.setText("Teléfono 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 10, 450);
        jPanel2.add(lblPhone1, gridBagConstraints);

        txtPhone1.addActionListener(this::txtPhone1ActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 450);
        jPanel2.add(txtPhone1, gridBagConstraints);

        lblPhone2.setText("Teléfono 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -398, 10, 350);
        jPanel2.add(lblPhone2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -400, 0, 350);
        jPanel2.add(txtPhone2, gridBagConstraints);

        lblEmail.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -298, 10, 0);
        jPanel2.add(lblEmail, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, -300, 0, 0);
        jPanel2.add(txtEmail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelForm.add(jPanel2, gridBagConstraints);

        jPanelButtons.setOpaque(false);

        btnBack.setText("Volver");
        btnBack.addActionListener(this::btnBackActionPerformed);
        jPanelButtons.add(btnBack);

        filler3.setAlignmentX(1.0F);
        filler3.setAlignmentY(1.0F);
        jPanelButtons.add(filler3);

        btnAddClient.setText("Agregar");
        btnAddClient.addActionListener(this::btnAddClientActionPerformed);
        jPanelButtons.add(btnAddClient);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        jPanelForm.add(jPanelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(80, 40, 80, 40);
        jPanelCenter.add(jPanelForm, gridBagConstraints);

        add(jPanelCenter, java.awt.BorderLayout.CENTER);
        jPanelCenter.getAccessibleContext().setAccessibleParent(jPanelCenter);

        jPanelRight.setOpaque(false);
        jPanelRight.setPreferredSize(new java.awt.Dimension(200, 0));
        jPanelRight.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weighty = 1.0;
        jPanelRight.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        jPanelRight.add(filler2, gridBagConstraints);

        add(jPanelRight, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCommercialNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCommercialNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCommercialNameActionPerformed

    private void txtZipCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtZipCodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtZipCodeActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBackActionPerformed

    private void txtPhone1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhone1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhone1ActionPerformed

    private void txtLastName1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLastName1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLastName1ActionPerformed

    private void txtClientNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClientNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClientNumberActionPerformed

    private void btnAddClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddClientActionPerformed
        guardarCliente();
    }//GEN-LAST:event_btnAddClientActionPerformed

    private void setIconoBlanco(javax.swing.JButton btn, String rutaSvg) {
        com.formdev.flatlaf.extras.FlatSVGIcon icon = new com.formdev.flatlaf.extras.FlatSVGIcon(rutaSvg, 20, 20);
        // ESTO ES LA MAGIA: Fuerza el color blanco
        icon.setColorFilter(new com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter(color -> java.awt.Color.WHITE));
        btn.setIcon(icon);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddClient;
    private javax.swing.JButton btnBack;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelCenter;
    private javax.swing.JPanel jPanelForm;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblCity;
    private javax.swing.JLabel lblClientNumber;
    private javax.swing.JLabel lblDistrict;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblLastName1;
    private javax.swing.JLabel lblLastName2;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPhone1;
    private javax.swing.JLabel lblPhone2;
    private javax.swing.JLabel lblTaxName;
    private javax.swing.JLabel lblTaxNumber;
    private javax.swing.JLabel lblZipCode;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtClientNumber;
    private javax.swing.JTextField txtCommercialName;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLastName1;
    private javax.swing.JTextField txtLastName2;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhone1;
    private javax.swing.JTextField txtPhone2;
    private javax.swing.JTextField txtProvince;
    private javax.swing.JTextField txtTaxNumber;
    private javax.swing.JTextField txtZipCode;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JButton getBtnBack() {
        return btnBack;
    }

    public javax.swing.JButton getBtnAddClient() { // El botón "Agregar" del formulario
        return btnAddClient;
    }

    private void guardarCliente() {
        String nif = txtTaxNumber.getText().trim();

        // 1. Validaciones básicas
        if (nif.isEmpty() || txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIF y Nombre obligatorios.");
            return;
        }

        // 2. Validación NIF Inteligente
        // Solo verificamos duplicados si:
        // A) Estamos CREANDO (clienteEditar es null)
        // B) Estamos EDITANDO pero hemos cambiado el NIF (no es el mismo que teníamos)
        if (clienteEditar == null || !nif.equals(clienteEditar.getFiscalNumber())) {
            if (clienteDAO.existeNif(nif)) {
                JOptionPane.showMessageDialog(this, "El NIF ya existe.");
                return;
            }
        }

        try {
            // 3. Preparar el objeto
            Cliente c;

            if (clienteEditar != null) {
                c = clienteEditar; // Usamos el mismo objeto (mantiene su ID)
            } else {
                c = new Cliente(); // Objeto nuevo
                // El ID se genera solo en BBDD
            }

            // 4. Actualizar datos (Setters)
            c.setFiscalNumber(nif);
            c.setFirstName(txtName.getText().trim());
            c.setLastName1(txtLastName1.getText().trim());
            c.setLastName2(txtLastName2.getText().trim());
            c.setCommercialName(txtCommercialName.getText().trim());

            c.setAddress(txtAddress.getText().trim());
            c.setCity(txtCity.getText().trim());
            c.setZipCode(txtZipCode.getText().trim());
            c.setProvince(txtProvince.getText().trim());

            c.setPhone1(txtPhone1.getText().trim());
            c.setPhone2(txtPhone2.getText().trim());
            c.setEmail(txtEmail.getText().trim());

            c.calcularNombreFiscal();

            // 5. Guardar (merge sirve para Insert y Update)
            clienteDAO.guardar(c);

            JOptionPane.showMessageDialog(this, "Guardado correctamente.");

            // Volver atrás
            if (btnBack != null) {
                btnBack.doClick();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void cargarDatosEdicion() {
        if (clienteEditar != null) {
            // Cambiamos título o botón para que el usuario sepa que edita
            // btnAgregar.setText("Guardar Cambios"); 

            // Rellenar campos
            txtClientNumber.setText(String.valueOf(clienteEditar.getId()));
            txtTaxNumber.setText(clienteEditar.getFiscalNumber());
            txtName.setText(clienteEditar.getFirstName());
            txtLastName1.setText(clienteEditar.getLastName1());
            txtLastName2.setText(clienteEditar.getLastName2());
            txtCommercialName.setText(clienteEditar.getCommercialName());

            txtAddress.setText(clienteEditar.getAddress());
            txtCity.setText(clienteEditar.getCity());
            txtZipCode.setText(clienteEditar.getZipCode());
            txtProvince.setText(clienteEditar.getProvince());

            txtPhone1.setText(clienteEditar.getPhone1());
            txtPhone2.setText(clienteEditar.getPhone2());
            txtEmail.setText(clienteEditar.getEmail());

            // Bloquear ID 
            txtClientNumber.setEditable(false);
        }
    }
}

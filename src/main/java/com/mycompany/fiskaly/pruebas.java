/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.fiskaly;

import com.mycompany.fiskaly.Invoices.InvoicesManagement;
import java.io.IOException;

/**
 *
 * @author user
 */
public class pruebas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //Config.refrescarUUID();
        //Clients.getFirstClientID();
        
        Config.refrescarUUID();
        //String numFactura = InvoicesManagement.getInvoiceNumberByID("dcba7c10-aa59-4dcf-94e5-14d7035fcfc6");
        //System.out.println(numFactura);
        //InvoicesManagement.getRegistrationDescription(numFactura);

        Signers.createSigner();
        //Signers.getFirstSignerID();
        
    }

}

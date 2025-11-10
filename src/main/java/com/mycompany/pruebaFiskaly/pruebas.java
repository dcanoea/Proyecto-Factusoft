/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;

/**
 *
 * @author user
 */
public class pruebas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //InvoicesManagement.retrieveInvoice("11c53b7b-9d5e-4626-a479-5eea527eed33"); // nº 20251115
        //System.out.println(InvoicesManagement.getInvoiceIDByNumber("20251115")); // id 11c53b7b-9d5e-4626-a479-5eea527eed33
        //System.out.println(InvoicesManagement.getInvoiceNumberByID("11c53b7b-9d5e-4626-a479-5eea527eed33")); // 20251115

        System.out.println(InvoicesManagement.getInvoiceFullAmount("20251115"));
        
    }

}

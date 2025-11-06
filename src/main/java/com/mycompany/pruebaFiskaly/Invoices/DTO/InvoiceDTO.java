/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.util.List;

/**
 *
 * @author user
 */
public class InvoiceDTO {

    public String type;
    public DataCorrectingDTO data;
    public List<RecipientsDTO> recipients;

    public InvoiceDTO(DataCorrectingDTO data, List<RecipientsDTO> recipients) {
        this.data = data;
        this.recipients = recipients;
        this.type = "COMPLETE";
    }

    public InvoiceDTO() {
    }

}

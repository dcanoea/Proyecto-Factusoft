package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.util.List;

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

package com.mycompany.fiskaly.Invoices.DTO;

import java.util.List;

public class InvoiceDTO {

    public String type;
    public DataDTO data;
    public List<RecipientsDTO> recipients;

    public InvoiceDTO(DataDTO data, List<RecipientsDTO> recipients) {
        this.data = data;
        this.recipients = recipients;
        this.type = "COMPLETE";
    }

    public InvoiceDTO() {
        
    }

}


package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.util.List;

public class ContentCompleteDTO {

    public DataDTO data;
    public List<RecipientsDTO> recipients;
    public String type;

 
    /**
     * No args constructor for use in serialization
     * 
     */
    public ContentCompleteDTO() {
    }

    public ContentCompleteDTO(DataDTO data, List<RecipientsDTO> recipients) {
        super();
        this.data = data;
        this.recipients = recipients;
        this.type = "COMPLETE";
    }

    public DataDTO getData() {
        return data;
    }

    public List<RecipientsDTO> getRecipients() {
        return recipients;
    }

    public String getType() {
        return type;
    }

    
}

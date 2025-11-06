
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.util.List;

public class ContentCompleteDTO {

    public DataCompleteDTO data;
    public List<RecipientsDTO> recipients;
    public String type;

 
    /**
     * No args constructor for use in serialization
     * 
     */
    public ContentCompleteDTO() {
    }

    public ContentCompleteDTO(DataCompleteDTO data, List<RecipientsDTO> recipients) {
        super();
        this.data = data;
        this.recipients = recipients;
        this.type = "COMPLETE";
    }

}

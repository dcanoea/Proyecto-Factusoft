
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.util.ArrayList;
import java.util.List;

public class ContentDTO {

    public DataDTO data;
    public List<RecipientDTO> recipients;
    public String type;

 
    /**
     * No args constructor for use in serialization
     * 
     */
    public ContentDTO() {
    }

    public ContentDTO(DataDTO data, List<RecipientDTO> recipients) {
        super();
        this.data = data;
        this.recipients = recipients;
        this.type = "COMPLETE";
    }

}

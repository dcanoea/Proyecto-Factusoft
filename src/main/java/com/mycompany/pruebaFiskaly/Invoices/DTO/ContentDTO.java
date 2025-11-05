
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import java.util.ArrayList;
import java.util.List;

public class ContentDTO {

    public DataDTO data;
    public RecipientDTO recipients;
    public String type;

 
    /**
     * No args constructor for use in serialization
     * 
     */
    public ContentDTO() {
    }

    public ContentDTO(DataDTO data, RecipientDTO recipient) {
        super();
        this.data = data;
        this.recipients = recipient;
        this.type = "COMPLETE";
    }

}

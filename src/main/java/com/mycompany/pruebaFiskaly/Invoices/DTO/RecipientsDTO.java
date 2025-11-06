package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecipientsDTO {

    @JsonProperty("address_line")
    public String addressLine;
    public IdDTO id;
    @JsonProperty("postal_code")
    public String postalCode;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RecipientsDTO() {
    }

    public RecipientsDTO(String addressLine, IdDTO id, String postalCode) {
        super();
        this.addressLine = addressLine;
        this.id = id;
        this.postalCode = postalCode;
    }

}

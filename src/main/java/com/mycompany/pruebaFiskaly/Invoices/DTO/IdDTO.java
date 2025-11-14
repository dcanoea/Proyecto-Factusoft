package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdDTO {

    @JsonProperty("tax_number")
    public String taxNumber;
    public Boolean registered;
    @JsonProperty("legal_name")
    public String legalName;

    /**
     * No args constructor for use in serialization
     * 
     */
    public IdDTO() {
    }

    public IdDTO(String taxNumber, Boolean registered, String legalName) {
        super();
        this.taxNumber = taxNumber;
        this.registered = registered;
        this.legalName = legalName;
    }    
}

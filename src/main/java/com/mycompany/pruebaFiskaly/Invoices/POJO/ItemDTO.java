
package com.mycompany.pruebaFiskaly.Invoices.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemDTO {

    public String quantity;
    public SystemDTO system;
    public String discount;
    public String text;
    @JsonProperty("unit_amount")
    public String unitAmount;
    @JsonProperty("full_amount")
    public String fullAmount;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ItemDTO() {
    }

    public ItemDTO(String quantity, SystemDTO system, String discount, String text, String unitAmount, String fullAmount) {
        super();
        this.quantity = quantity;
        this.system = system;
        this.discount = discount;
        this.text = text;
        this.unitAmount = unitAmount;
        this.fullAmount = fullAmount;
    }
}
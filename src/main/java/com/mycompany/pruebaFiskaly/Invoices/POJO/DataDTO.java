
package com.mycompany.pruebaFiskaly.Invoices.POJO;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataDTO {

    public String number;
    public String text;
    public String type;
    public List<ItemDTO> items = new ArrayList<ItemDTO>();
    @JsonProperty("full_amount")
    public String fullAmount;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DataDTO() {
    }

    public DataDTO(String number, String text, String type, List<ItemDTO> items, String fullAmount) {
        super();
        this.number = number;
        this.text = text;
        this.type = type;
        this.items = items;
        this.fullAmount = fullAmount;
    }

}

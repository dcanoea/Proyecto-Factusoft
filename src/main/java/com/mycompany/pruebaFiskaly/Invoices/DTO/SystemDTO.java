package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDTO {

    public Type type;
    public CategoryDTO category;

    public enum Type {
        @JsonProperty("REGULAR")
        REGULAR,
        @JsonProperty("OTHER_TAX_IVA")
        OTHER_TAX_IVA,
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public SystemDTO() {
    }

    public SystemDTO(Type type, CategoryDTO category) {
        super();
        this.type = type;
        this.category = category;
    }

}

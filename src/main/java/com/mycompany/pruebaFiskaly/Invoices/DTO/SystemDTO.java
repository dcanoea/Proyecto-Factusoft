package com.mycompany.pruebaFiskaly.Invoices.DTO;

public class SystemDTO {

    public Type type;
    public CategoryDTO category;

    public enum Type {
        REGULAR, //Operaciones en régimen regular
        OTHER_TAX_IVA //Operaciones sujetas a otro tipo de impuesto territorial (IVA)
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

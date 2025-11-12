package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // No incluir campos con valor null
public class CategoryDTO {

    public Cause cause;
    public Type type;
    public Rate rate;

    public enum Type {
        @JsonProperty("VAT")
        VAT,
        @JsonProperty("NO_VAT")
        NO_VAT
    }

    public enum Rate {
        @JsonProperty("0")
        IVA_0("0"),
        @JsonProperty("4")
        IVA_4("4"),
        @JsonProperty("10")
        IVA_10("10"),
        @JsonProperty("21")
        IVA_21("21");

        private final String value;

        Rate(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum Cause {
        @JsonProperty("TAXABLE_EXEMPT_1") // Exenta por el artículo 20 de la Ley de IVA. 
        TAXABLE_EXEMPT_1,
        @JsonProperty("TAXABLE_EXEMPT_2")
        TAXABLE_EXEMPT_2,
        @JsonProperty("TAXABLE_EXEMPT_3")
        TAXABLE_EXEMPT_3,
        @JsonProperty("TAXABLE_EXEMPT_4")
        TAXABLE_EXEMPT_4
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public CategoryDTO() {
    }

    // Constructor para VAT, si se pasa IVA 0, lo trata como exento 1(se usa habitualmente en ACF)
    public CategoryDTO(Rate rate) {
        this.type = Type.VAT;
        this.rate = rate;
        if (rate == Rate.IVA_0){
            this.type = Type.NO_VAT;
            this.rate = null;
            this.cause = Cause.TAXABLE_EXEMPT_1;
        }
    }
    
    // Constructor para NO_VAT, para tratar otros caso de exención
    public CategoryDTO(Cause cause){
        this.type = Type.NO_VAT;
        this.rate = null;
        this.cause = cause;
    }

    public Cause getCause() {
        return cause;
    }

    public Type getType() {
        return type;
    }

    public Rate getRate() {
        return rate;
    }

    
}

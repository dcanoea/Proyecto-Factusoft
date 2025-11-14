package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // No incluir campos con valor null
public class CategoryDTO {

    public Cause cause;
    public Type type;
    public Rate rate;

    public enum Type {
        VAT,
        NO_VAT, // PUEDE SE UN TIPO DE EXENCIÓN O NO IMPONIBLE
        INVERSE_VAT
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
        TAXABLE_EXEMPT_1, // Exenta por el artículo 20 de la Ley de IVA. 
        TAXABLE_EXEMPT_2, // Exenta por el artículo 21 de la Ley de IVA.
        TAXABLE_EXEMPT_3, // Exenta por el artículo 22 de la Ley de IVA.
        TAXABLE_EXEMPT_4, // Exenta por el artículo 23 y 24 de la Ley de IVA.
        TAXABLE_EXEMPT_5, // Exenta por el artículo 25 de la Ley de IVA.
        TAXABLE_EXEMPT_6, // Exenta por otra causa.
        TAXABLE_EXEMPT_7, // Aplicable únicamente para el tipo impositivo IGIC y se trata de una causa de exención conforme al artículo 110 de la Ley 4/2012.
        TAXABLE_EXEMPT_8, // Aplicable únicamente para el tipo impositivo IGIC y cubre otras causas de exención.
        NON_TAXABLE_1, // es una operación no sujeta por el artículo 7 de la Ley del IVA. Otros supuestos de no sujeción. 
        NON_TAXABLE_2, // es una operación no sujeta por reglas de localización
        NON_TAXABLE_3, // es una operación no sujeta en el TAI por reglas de localización, pero repercute impuesto extranjero, IPSI/IGIC o IVA.
        NON_TAXABLE_4 // es una operación no sujeta por ventas realizadas por cuenta de terceros
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
        if (rate == Rate.IVA_0) {
            this.type = Type.NO_VAT;
            this.rate = null;
            this.cause = Cause.TAXABLE_EXEMPT_1;
        }
    }

    // Constructor para NO_VAT, para tratar otros caso de exención
    public CategoryDTO(Cause cause) {
        this.type = Type.NO_VAT;
        this.rate = null;
        this.cause = cause;
    }
}

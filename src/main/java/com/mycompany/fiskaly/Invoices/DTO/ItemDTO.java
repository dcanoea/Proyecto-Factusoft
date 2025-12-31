package com.mycompany.fiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL) // No incluir campos con valor null
public class ItemDTO {

    public String quantity;
    public SystemDTO system;
    public String discount;
    public String text;
    @JsonProperty("unit_amount")
    public String unitAmount;
    @JsonProperty("vat_type")
    public VatType vatype;
    @JsonProperty("full_amount")
    public String fullAmount;

    public enum VatType {
        IVA, OTHER
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public ItemDTO() {
    }

    public ItemDTO(String quantity, SystemDTO system, String discount, String text, String unitAmount, VatType vatType) {
        this.quantity = quantity;
        this.system = system;
        this.discount = discount;
        this.text = text;
        this.unitAmount = unitAmount;
        this.vatype = vatType;

        // Calculo del importe total
        try {
            double qty = Double.parseDouble(quantity);
            double dct = Double.parseDouble(discount);
            if (dct == 0) {
                this.discount = null;
            }
            double unit = Double.parseDouble(unitAmount);
            double base = (unit - dct) * qty;

            double iva = 0;
            if (system != null && system.category != null && system.category.rate != null) {
                // Convertimos el rate del CategoryDTO a número
                iva = Double.parseDouble(system.category.rate.toString());
            }

            double total = base * (1 + iva / 100);
            this.fullAmount = String.format(Locale.US, "%.2f", total); // con 2 decimales y . como separador decimal (requerido por API Fiskaly)
        } catch (NumberFormatException e) {
            this.fullAmount = "0.00"; // fallback seguro
        }
    }

    public String getFullAmount() {
        return fullAmount;
    }
}

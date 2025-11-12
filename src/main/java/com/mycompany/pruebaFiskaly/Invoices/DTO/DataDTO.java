package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Locale;

public class DataDTO {

    @JsonProperty("number")
    public String invoiceNumber;
    public String text;
    public String type;
    public List<ItemDTO> items = new ArrayList<ItemDTO>();
    @JsonProperty("full_amount")
    public String fullAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String series;

    /**
     * No args constructor for use in serialization
     *
     */
    public DataDTO() {
    }

    public DataDTO(String invoiceNumber, String text, List<ItemDTO> items) {
        this.invoiceNumber = invoiceNumber;
        this.text = text;
        this.type = "SIMPLIFIED"; // SIMPLIFIED incluso para facturas completas
        this.items = items;

        // Cálculo del total de la factura, recogiendo los totales de cada item
        double total = 0;
        if (items != null) {
            for (ItemDTO item : items) {
                try {
                    total += Double.parseDouble(item.getFullAmount());
                } catch (NumberFormatException e) {
                    // Si algún item tiene un valor inválido, se ignora
                    total += 0;
                }
            }
        }

        // Guardar total con 2 decimales y . como separador decimal (requerido por la API Fiskaly)
        this.fullAmount = String.format(Locale.US, "%.2f", total);
    }

    public DataDTO(String invoiceNumber, String text, List<ItemDTO> items, String series) {
        this(invoiceNumber, text, items);
        this.series = series;
    }
}

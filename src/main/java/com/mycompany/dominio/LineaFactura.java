package com.mycompany.dominio;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lineas_factura")
public class LineaFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // RELACIÓN CON FACTURA (FK)
    @ManyToOne
    @JoinColumn(name = "id_factura", nullable = false)
    private Factura factura;

    // RELACIÓN CON PRODUCTO (FK)
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto; // Puede ser null si es un concepto manual

    private String description;
    private BigDecimal quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "tax_percent")
    private BigDecimal taxPercent;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "total_line")
    private BigDecimal totalLine;

    public LineaFactura() {
    }

    // GETTERS Y SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(BigDecimal totalLine) {
        this.totalLine = totalLine;
    }

}

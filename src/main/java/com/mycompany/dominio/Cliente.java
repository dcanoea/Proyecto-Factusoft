package com.mycompany.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Identificación
    @Column(name = "fiscal_number")
    private String fiscalNumber; // DNI/NIE/CIF

    // Datos Persona Física
    @Column(name = "first_name")
    private String firstName;    // Nombre

    @Column(name = "last_name_1")
    private String lastName1;    // Apellido 1

    @Column(name = "last_name_2")
    private String lastName2;    // Apellido 2

    // Datos Fiscales / Empresa
    @Column(name = "fiscal_name")
    private String fiscalName;   // Nombre completo o Razón Social (Este usaremos para listados y Fiskaly)

    @Column(name = "commercial_name")
    private String commercialName; // Nombre Comercial

    // Contacto y Ubicación
    private String email;

    @Column(name = "phone_1")
    private String phone1;

    @Column(name = "phone_2")
    private String phone2;

    private String address;
    private String city;     // Ciudad
    private String province; // Provincia

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Cliente() {
    }

    // Método para rellenar el fiscalName automáticamente si es persona física, util para Fiskaly
   public void calcularNombreFiscal() {
        // Construimos el nombre con: Nombre + Apellido1 + Apellido2 (si existe)
        String nombreCompleto = (this.firstName + " " + this.lastName1 + " " + 
                                (this.lastName2 != null ? this.lastName2 : "")).trim();
        
        this.fiscalName = nombreCompleto;
    }

    @Override
    public String toString() {
        return fiscalName;
    }

    // --- GETTERS Y SETTERS 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName1() {
        return lastName1;
    }

    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public String getFiscalName() {
        return fiscalName;
    }

    public void setFiscalName(String fiscalName) {
        this.fiscalName = fiscalName;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.mycompany.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;
    private String password; // Recuerda: idealmente hash, no texto plano

    @Column(name = "fiscal_name")
    private String fiscalName;

    @Column(name = "commercial_name")
    private String commercialName;

    @Column(name = "fiscal_number")
    private String fiscalNumber; // CIF/NIF

    private String address;
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    private String country;

    @Column(name = "logo_path")
    private String logoPath;

    // Configuración Fiskaly
    @Column(name = "fiskaly_api_key")
    private String fiskalyApiKey;

    @Column(name = "fiskaly_api_secret")
    private String fiskalyApiSecret;

    @Column(name = "fiskaly_token", columnDefinition = "TEXT")
    private String fiskalyToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // CONSTRUCTORES VACÍO Y CON PARÁMETROS
    public Empresa() {
    }

    public Empresa(int id, String email, String password, String fiscalName, String commercialName, String fiscalNumber, String address, String city, String zipCode, String country, String logoPath, String fiskalyApiKey, String fiskalyApiSecret, String fiskalyToken, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fiscalName = fiscalName;
        this.commercialName = commercialName;
        this.fiscalNumber = fiscalNumber;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.logoPath = logoPath;
        this.fiskalyApiKey = fiskalyApiKey;
        this.fiskalyApiSecret = fiskalyApiSecret;
        this.fiskalyToken = fiskalyToken;
        this.createdAt = createdAt;
    }
    
    

    // GETTERS Y SETTERS 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getFiskalyApiKey() {
        return fiskalyApiKey;
    }

    public void setFiskalyApiKey(String fiskalyApiKey) {
        this.fiskalyApiKey = fiskalyApiKey;
    }

    public String getFiskalyApiSecret() {
        return fiskalyApiSecret;
    }

    public void setFiskalyApiSecret(String fiskalyApiSecret) {
        this.fiskalyApiSecret = fiskalyApiSecret;
    }

    public String getFiskalyToken() {
        return fiskalyToken;
    }

    public void setFiskalyToken(String fiskalyToken) {
        this.fiskalyToken = fiskalyToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}

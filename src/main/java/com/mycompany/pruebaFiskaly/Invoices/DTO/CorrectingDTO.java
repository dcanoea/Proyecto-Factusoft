/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;

/**
 *
 * @author user
 */
public class CorrectingDTO {

    public ContentCorrectingDTO content;

    public CorrectingDTO() {
    }

    public CorrectingDTO(ContentCorrectingDTO content) {
        this.content = content;
    }
}

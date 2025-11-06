/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class PruebasDTO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //String numFactura = "20251113"; //completas
        String numFactura = "0003"; //rectificativas
        

        ArrayList<ItemDTO> items = new ArrayList<>();
        // item 1
        CategoryDTO pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);
        SystemDTO pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item1 = new ItemDTO("1", pruebaSystem, "-10", "Curso B", "-250", ItemDTO.VatType.IVA);
        items.add(item1);
        /*// item 2   NO FUNCIONA (CONSULTAR GESTORÍA PARA IMPLEMENTAR UN MÉTODO O OTRO (OTHER_TAX_IVA O REGULAR)
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_0);
        pruebaSystem = new SystemDTO(SystemDTO.Type.OTHER_TAX_IVA, pruebaCategory);
        ItemDTO item2 = new ItemDTO("1", pruebaSystem, "20", "Curso C", "350", ItemDTO.VatType.OTHER);
        items.add(item2);*/
        // item 2
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_0);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item2 = new ItemDTO("1", pruebaSystem, "-20", "Curso C", "-350", ItemDTO.VatType.IVA);
        items.add(item2);
        // item 3
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_4);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item3 = new ItemDTO("1", pruebaSystem, "0", "Libro C", "-35", ItemDTO.VatType.IVA);
        items.add(item3);
        // item 4 descuento global
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item4 = new ItemDTO("1", pruebaSystem, "0", "Descuento", "50", ItemDTO.VatType.IVA);
        items.add(item4);
        // item 5 
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_10);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item5 = new ItemDTO("10", pruebaSystem, "0", "Clases camion", "-60", ItemDTO.VatType.IVA);
        items.add(item5);
        //DATA
        DataDTO data = new DataDTO(numFactura, "Factura Completa", items);
        //RECEPTOR FACTURA
        ArrayList<RecipientsDTO> receptors = new ArrayList<>();
        IdDTO idReceptor = new IdDTO("B22260863", Boolean.TRUE, "ARAGON FORMACION ACF S.L.");
        RecipientsDTO receptor = new RecipientsDTO("C/ Comercio 28", idReceptor, "22006");
        receptors.add(receptor);
        //CONTENT
        ContentCompleteDTO content = new ContentCompleteDTO(data, receptors);
        //CREACION JSON FACTURA
        CompleteDTO completa = new CompleteDTO(content);
        //CreateCompleteInvoiceDTO.createInvoice(completa);
        
        //FACTURA RECTIFICATIVA
        DataCorrectingDTO dataCorrecting = new DataCorrectingDTO(numFactura, "Factura Abono", items);
        InvoiceDTO invoice = new InvoiceDTO(dataCorrecting, receptors);
        ContentCorrectingDTO contentCorrecting = new ContentCorrectingDTO(ContentCorrectingDTO.Method.SUBSTITUTION, 
                ContentCorrectingDTO.Code.CORRECTION_1, "0002", invoice);
        CorrectingDTO correcting = new CorrectingDTO(contentCorrecting);
        CreateCorrectingInvoiceDTO.createCorrectingInvoice(correcting);
    }

}

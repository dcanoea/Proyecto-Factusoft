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

        try {
            ArrayList<ItemDTO> items = new ArrayList<>();
            // item 1
            CategoryDTO pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);
            SystemDTO pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
            ItemDTO item1 = new ItemDTO("1", pruebaSystem, "10", "Curso B", "250");
            items.add(item1);

            // item 2
            pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_0);
            pruebaSystem = new SystemDTO(SystemDTO.Type.OTHER_TAX_IVA, pruebaCategory);
            ItemDTO item2 = new ItemDTO("1", pruebaSystem, "20", "Curso C", "350");
            items.add(item2);

            // item 3
            pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_4);
            pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
            ItemDTO item3 = new ItemDTO("1", pruebaSystem, "0", "Libro C", "35");
            items.add(item3);

            // item 4 descuento global
            pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);
            pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
            ItemDTO item4 = new ItemDTO("1", pruebaSystem, "0", "Descuento", "-50");
            items.add(item4);

            //DATA 
            DataDTO data = new DataDTO("1", "Factura completa de prueba", items);

            //RECEPTOR FACTURA
            IdDTO idReceptor = new IdDTO("B22260863", Boolean.TRUE, "ARAGON FORMACION ACF S.L.");
            RecipientDTO receptor = new RecipientDTO("C/ Comercio 28", idReceptor, "22006");
            
            //CONTENT 
            ContentDTO content = new ContentDTO(data, receptor);

            //CREACION JSON FACTURA
            CompleteDTO completa = new CompleteDTO(content);
            
            System.out.println(JsonUtil.toJson(completa));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(PruebasDTO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

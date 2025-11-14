/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices.LLamadas.Complete;

import com.mycompany.pruebaFiskaly.Invoices.CreateCompleteInvoice;
import com.mycompany.pruebaFiskaly.Invoices.DTO.CategoryDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ContentCompleteDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.DataDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.IdDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ItemDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.RecipientsDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.SystemDTO;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author user
 */
public class facturaExiste {

    //RECEPTOR FACTURA
    ArrayList<RecipientsDTO> receptors = new ArrayList<>();
    IdDTO idReceptor = new IdDTO("B22260863", Boolean.TRUE, "ARAGON FORMACION ACF S.L.");
    RecipientsDTO receptor = new RecipientsDTO("C/ Comercio 28", idReceptor, "22006");

    //ARRAY ITEMS
    ArrayList<ItemDTO> items = new ArrayList();

    public facturaExiste() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of createInvoice method, of class CreateCompleteInvoice.
     */
    @Test

    public void testFacturaExiste() {
        System.out.println("createInvoice");

        CategoryDTO pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);

        SystemDTO pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);

        ItemDTO item1 = new ItemDTO("1", pruebaSystem, "10", "Curso B", "250", ItemDTO.VatType.IVA);
        items.add(item1);
        DataDTO data = new DataDTO("1", "Factura Completa", items); // Factura nº 1

        receptors.add(receptor);

        ContentCompleteDTO content = new ContentCompleteDTO(data, receptors);

        int responseCode = CreateCompleteInvoice.createInvoice(content);

        assertTrue(responseCode == 409, "Código respuesta API: " + responseCode);
    }

}

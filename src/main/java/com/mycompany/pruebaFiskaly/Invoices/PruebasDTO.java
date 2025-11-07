package com.mycompany.pruebaFiskaly.Invoices;

import com.mycompany.pruebaFiskaly.Invoices.DTO.CategoryDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.CompleteDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ContentCompleteDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ContentCorrectingDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.CorrectingDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.DataCorrectingDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.DataCompleteDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.IdDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.InvoiceDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.ItemDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.RecipientsDTO;
import com.mycompany.pruebaFiskaly.Invoices.DTO.SystemDTO;
import java.util.ArrayList;

public class PruebasDTO {
    public static void main(String[] args) {

        String numFactura = "20251115"; //completas
        //String numFactura = "0003"; //rectificativas
        

        ArrayList<ItemDTO> items = new ArrayList<>();
        // item 1
        CategoryDTO pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);
        SystemDTO pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item1 = new ItemDTO("1", pruebaSystem, "10", "Curso B", "250", ItemDTO.VatType.IVA);
        items.add(item1);
        /*// item 2   NO FUNCIONA (CONSULTAR GESTORÍA PARA IMPLEMENTAR UN MÉTODO O OTRO (OTHER_TAX_IVA O REGULAR)
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_0);
        pruebaSystem = new SystemDTO(SystemDTO.Type.OTHER_TAX_IVA, pruebaCategory);
        ItemDTO item2 = new ItemDTO("1", pruebaSystem, "20", "Curso C", "350", ItemDTO.VatType.OTHER);
        items.add(item2);*/
        // item 2
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_0);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item2 = new ItemDTO("1", pruebaSystem, "20", "Curso C", "350", ItemDTO.VatType.IVA);
        items.add(item2);
        // item 3
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_4);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item3 = new ItemDTO("1", pruebaSystem, "0", "Libro C", "35", ItemDTO.VatType.IVA);
        items.add(item3);
        // item 4 descuento global
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_21);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item4 = new ItemDTO("1", pruebaSystem, "0", "Descuento", "-50", ItemDTO.VatType.IVA);
        items.add(item4);
        // item 5 
        pruebaCategory = new CategoryDTO(CategoryDTO.Rate.IVA_10);
        pruebaSystem = new SystemDTO(SystemDTO.Type.REGULAR, pruebaCategory);
        ItemDTO item5 = new ItemDTO("10", pruebaSystem, "0", "Clases camion", "60", ItemDTO.VatType.IVA);
        items.add(item5);
        //DATA
        DataCompleteDTO data = new DataCompleteDTO(numFactura, "Factura Completa", items);
        //RECEPTOR FACTURA
        ArrayList<RecipientsDTO> receptors = new ArrayList<>();
        IdDTO idReceptor = new IdDTO("B22260863", Boolean.TRUE, "ARAGON FORMACION ACF S.L.");
        RecipientsDTO receptor = new RecipientsDTO("C/ Comercio 28", idReceptor, "22006");
        receptors.add(receptor);
        //CONTENT
        ContentCompleteDTO content = new ContentCompleteDTO(data, receptors);
        //CREACION JSON FACTURA
        CompleteDTO completa = new CompleteDTO(content);
        CreateCompleteInvoice.createInvoice(completa);
        
        //FACTURA RECTIFICATIVA
        DataCorrectingDTO dataCorrecting = new DataCorrectingDTO(numFactura, "Factura Abono", items);
        InvoiceDTO invoice = new InvoiceDTO(dataCorrecting, receptors);
        ContentCorrectingDTO contentCorrecting = new ContentCorrectingDTO(ContentCorrectingDTO.Method.SUBSTITUTION, 
                ContentCorrectingDTO.Code.CORRECTION_1, "0002", invoice);
        CorrectingDTO correcting = new CorrectingDTO(contentCorrecting);
        //CreateCorrectingInvoice.createCorrectingInvoice(correcting);
    }

}

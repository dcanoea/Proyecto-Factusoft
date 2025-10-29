package com.mycompany.pruebaFiskaly;

import com.mycompany.pruebaFiskaly.Invoices.Complete;
import com.mycompany.pruebaFiskaly.Invoices.Correcting;
import com.mycompany.pruebaFiskaly.Invoices.InvoiceHelpers;
import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;
import com.mycompany.pruebaFiskaly.Invoices.Simplified;
import com.mycompany.pruebaFiskaly.Invoices.Summary;
import com.mycompany.pruebaFiskaly.Invoices.SummaryCOMPLETES;
import static java.awt.SystemColor.text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pruebas {

    public static int NUM_FACTURA = 20250128;

    public static void main(String[] args) throws IOException {

//-------------------------------------------------------------------------------------------------------------------------------
        //Validation.Java
        //VALIDACIÓN NIF AEAT
        //System.out.println(Validation.validateAEAT("T00000001"));
        //System.out.println(Validation.validateAEAT("T00000002"));
        //System.out.println(Validation.validateAEAT("T00000003"));
        //System.out.println(Validation.validateAEAT("T00000004"));
        //VALIDACIÓN NIF VIES
        //System.out.println(Validation.validateVIES("ES", "B44752210"));
        //System.out.println(Validation.validateVIES("ES", "B4433333210"));   
//-------------------------------------------------------------------------------------------------------------------------------
        //INVOICES
        // OBTENER DETALLES DE UNA FACTURA
        //String invoiceid = InvoicesManagement.getInvoiceIDByNumber("20250119");
        //InvoicesManagement.retrieveInvoice(invoiceid);
        // OBTENER ESTADO DE ERROR DE LA FACTURA Y SU DESCRIPCION
        //InvoicesManagement.getRegistrationDescription("20250074", InvoicesManagement.getInvoiceIDByNumber("20250074"));
        List<JSONObject> itemsList = new ArrayList<>();
        itemsList.add(InvoiceHelpers.createItem("Prueba1", "1", "0", "111", Config.IVA_EXENTO));
        itemsList.add(InvoiceHelpers.createItem("Prueba2", "2", "10", "222", Config.IVA_SUPERREDUCIDO));
        itemsList.add(InvoiceHelpers.createItem("Prueba3", "3", "20", "333", Config.IVA_GENERAL));
        itemsList.add(InvoiceHelpers.createItem("Prueba4", "4", "15", "333", Config.IVA_SUPERREDUCIDO));
        itemsList.add(InvoiceHelpers.createItem("Prueba5", "5", "0", "333", Config.IVA_GENERAL));
        itemsList.add(InvoiceHelpers.createItem("Prueba6", "6", "5", "333", Config.IVA_EXENTO));

        List<JSONObject> globalDiscounts = new ArrayList<>();
        globalDiscounts.add(InvoiceHelpers.createGlobalDiscount(Config.IVA_GENERAL, "1", "-50"));
        globalDiscounts.add(InvoiceHelpers.createGlobalDiscount(Config.IVA_SUPERREDUCIDO, "1", "-50"));
        globalDiscounts.add(InvoiceHelpers.createGlobalDiscount(Config.IVA_EXENTO, "1", "-50"));

        List<JSONObject> suppliedItems = new ArrayList<>();
        //JSONObject supplied = Complete.createSupplied("RECARGA TARJETA MUGI", "1", "50", "50");
        //suppliedItems.add(supplied);

        Map<String, String> receptorDetails = InvoiceHelpers.createReceptor("ARAGON FORMACION ACF S.L", "B22260863", true, "C/Comercio 28", "22000");

        // CREAR FACTURA COMPLETA
        Complete.createCompleteInvoice(String.valueOf(NUM_FACTURA), itemsList, suppliedItems, globalDiscounts, receptorDetails);

        // CREAR FACTURAS RECTIFICATIVAS
        //Correcting.createCorrectingInvoiceSubstitutionComplete(20250120, NUM_FACTURA, itemsList, suppliedItems, globalDiscounts, receptorDetails);
        // RECUPERAR TOTAL FACTURA 
        //String idFactura = InvoicesManagement.getInvoiceIDByNumber("S-2025-009"); //7891d62c-7eba-40e2-a058-405d8a2b4718
        //System.out.println(InvoicesManagement.getFullAmount(idFactura));
        // FACTURA RECAPITULATIVA
        /*List<String> numerosFacturas = new ArrayList<>();
        int numFactura = 20250051;
        for (int i = 0; i < 10; i++) {
            numerosFacturas.add(String.valueOf(numFactura));
            numFactura++;
        }
        Summary.createSummaryInvoice(NUM_FACTURA, numerosFacturas);*/
        //SummaryCOMPLETES.createSummaryCompleteInvoice(NUM_FACTURA, numerosFacturas);
        //MÉTODO CANCELAR FACTURA (FUNCIONA, PERO NO VEO CUANDO SE PUEDE USAR)
        //InvoicesManagement.cancelInvoice("20250063");
    }
}

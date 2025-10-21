/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pruebaFiskaly.Invoices;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.pruebaFiskaly.Authentication;
import com.mycompany.pruebaFiskaly.Clients;
import com.mycompany.pruebaFiskaly.Config;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class Complete {
    
    // FALTA AÑADIR VALIDACIÓN NIF
    // FALTA CREAR UN METODO PARA HACER FACTURAS RECAPITULATIVAS (SUSTITUYE A MULTIPLES SIMPLIFICADAS DEL MISMO CLIENTE)
    // SE DEBE VALIDAR EL NIF, SINO LA FACTURA SE SUBE PERO EN ESTADO DE REVISIÓN
    public static void create_Complete_Invoice() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.get_First_Client_Id();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = "C-2025-004";
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieve_token();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DEL ÍTEM =========
            String text = "Curso ADR";
            String quantity = "1.00";
            String unitAmount = "210.74";
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
            String fullAmount = String.format(Locale.US, "%.2f", full);

            // ======== SISTEMA FISCAL =========
            JSONObject category = new JSONObject();
            category.put("type", "VAT");
            category.put("rate", ivaRate);

            JSONObject system = new JSONObject();
            system.put("type", "REGULAR");
            system.put("category", category);

            // ======== ÍTEM =========
            JSONObject item = new JSONObject();
            item.put("text", text);
            item.put("quantity", quantity);
            item.put("unit_amount", unitAmount);
            item.put("full_amount", fullAmount);
            item.put("system", system);

            JSONArray items = new JSONArray();
            items.put(item);

            // ======== RECEPTOR =========
            JSONObject id = new JSONObject();
            id.put("legal_name", "ARAGON FORMACION ACF S.L.");
            id.put("tax_number", "B22260863");
            id.put("registered", true);

            JSONObject recipient = new JSONObject();
            recipient.put("id", id);
            recipient.put("address_line", "Calle Mayor 123, Huesca");
            recipient.put("postal_code", "22001");

            JSONArray recipients = new JSONArray();
            recipients.put(recipient);

            // ======== CONTENIDO DE DATA =========
            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED"); // requerido por el esquema Fiskaly
            data.put("number", invoice_number);
            data.put("text", "Factura COMPLETA por formación ADR");
            data.put("items", items);
            data.put("full_amount", fullAmount);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "COMPLETE");
            content.put("recipients", recipients);
            content.put("data", data);

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            // ======== PETICIÓN HTTP =========
            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);
            JSONObject jsonPrint = new JSONObject(responseBody);
            System.out.println(jsonPrint.toString(2)); // Indentación de 2 espacios

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject json = new JSONObject(responseBody).getJSONObject("content");
                JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
                String qrBase64 = qr.getString("data");

                generateCompleteInvoicePDF(invoice_number, "Factura COMPLETA por formación ADR", "ARAGON FORMACION ACF S.L.",
                        "B22260863", "Calle Mayor 123, Huesca", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
            } else {
                System.err.println("Error al crear la factura completa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura completa");
            e.printStackTrace();
        }
    }
    
    
     public static void generateCompleteInvoicePDF(
            String number, String description, String clientName, String clientNIF, String clientAddress,
            String itemText, String quantity, String unitAmount, String ivaRate, String fullAmount, String qrBase64) {

        try {
            String desktopPath = System.getProperty("user.home") + "/Desktop/";
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String fileName = "Factura_Completa_" + number + "_" + date + ".pdf";

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
            document.open();

            // ======== CABECERA ========
            Paragraph title = new Paragraph("FACTURA COMPLETA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Número: " + number));
            document.add(new Paragraph("Fecha de emisión: " + date));
            document.add(new Paragraph("Descripción general: " + description));
            document.add(Chunk.NEWLINE);

            // ======== DATOS DEL CLIENTE ========
            Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(clientHeader);
            document.add(new Paragraph("Nombre o Razón Social: " + clientName));
            document.add(new Paragraph("NIF: " + clientNIF));
            document.add(new Paragraph("Dirección: " + clientAddress));
            document.add(Chunk.NEWLINE);

            // ======== TABLA DE ÍTEMS ========
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Stream.of("Descripción", "Cantidad", "Precio Unitario", "IVA (%)", "Total con IVA")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                        table.addCell(header);
                    });

            table.addCell(itemText);
            table.addCell(quantity);
            table.addCell(unitAmount + " €");
            table.addCell(ivaRate);
            table.addCell(fullAmount + " €");

            document.add(table);

            // ======== TOTALES ========
            Paragraph total = new Paragraph("Importe total (IVA incluido): " + fullAmount + " €",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            document.add(Chunk.NEWLINE);

            // ======== QR FISCAL ========
            byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.scaleToFit(120, 120);
            qrImage.setAlignment(Image.ALIGN_RIGHT);
            document.add(new Paragraph("\nCódigo QR fiscal:"));
            document.add(qrImage);

            // ======== PIE DE PÁGINA ========
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Factura emitida electrónicamente conforme a la normativa Verifactu.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));
            document.add(new Paragraph("Gracias por su confianza.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

            document.close();
            System.out.println("PDF de factura completa guardado en: " + desktopPath + fileName);
        } catch (Exception ex) {
            System.out.print("Error al crear factura ");
            ex.printStackTrace();
        }
    }
}

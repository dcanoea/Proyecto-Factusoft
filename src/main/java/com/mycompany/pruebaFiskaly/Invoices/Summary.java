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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

public class Summary {

    public static void createSummaryInvoice(List<String> numerosFactura) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String client_id = Clients.getFirstClientID();
            UUID invoice_id = UUID.randomUUID();
            String invoice_number = "R-" + invoice_id.toString().substring(0, 8).toUpperCase();
            String url = Config.BASE_URL + "/clients/" + client_id + "/invoices/" + invoice_id;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            JSONArray items = new JSONArray();
            JSONArray summarizedInvoices = new JSONArray();
            Map<String, String> facturaImportes = new LinkedHashMap<>();
            double totalBaseImponible = 0.0;
            double tipoIVA = 21.0;

            for (String numero : numerosFactura) {
                String uuid = InvoicesManagement.getInvoiceID(numero);
                String importeStr = InvoicesManagement.getFullAmount(uuid);

                if (importeStr == null || importeStr.trim().isEmpty()) {
                    System.err.println("⚠️ Importe vacío para factura " + numero);
                    continue;
                }

                double importeConIVA = Double.parseDouble(importeStr);
                double baseImponible = importeConIVA / (1 + tipoIVA / 100.0);
                baseImponible = Math.round(baseImponible * 100.0) / 100.0; // Redondeo a 2 decimales
                totalBaseImponible += baseImponible;

                facturaImportes.put(numero, String.format(Locale.US, "%.2f", importeConIVA));

                JSONObject item = new JSONObject();
                item.put("text", "Factura " + numero);
                item.put("quantity", "1.00");
                item.put("unit_amount", String.format(Locale.US, "%.2f", baseImponible));
                item.put("full_amount", String.format(Locale.US, "%.2f", importeConIVA));

                JSONObject category = new JSONObject();
                category.put("type", "VAT");
                category.put("rate", String.valueOf(tipoIVA));

                JSONObject system = new JSONObject();
                system.put("type", "REGULAR");
                system.put("category", category);

                item.put("system", system);
                items.put(item);
                summarizedInvoices.put(uuid);
            }

            double cuotaIVA = totalBaseImponible * (tipoIVA / 100.0);
            double totalFactura = totalBaseImponible + cuotaIVA;
            String fullAmount = String.format(Locale.US, "%.2f", totalFactura);

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

            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED");
            data.put("number", invoice_number);
            data.put("text", "Factura recapitulativa de formación ADR");
            data.put("items", items);
            data.put("full_amount", fullAmount);

            JSONObject content = new JSONObject();
            content.put("type", "COMPLETE");
            content.put("recipients", recipients);
            content.put("data", data);
            content.put("summarized_invoices", summarizedInvoices);

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println("Respuesta del servidor: " + responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject json = new JSONObject(responseBody).getJSONObject("content");
                JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
                String qrBase64 = qr.getString("data");

                generateRecapInvoicePDF(invoice_number, "Factura recapitulativa de formación ADR",
                        "ARAGON FORMACION ACF S.L.", "B22260863", "Calle Mayor 123, Huesca",
                        facturaImportes, fullAmount, qrBase64);
            } else {
                System.err.println("Error al crear la factura recapitulativa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura recapitulativa");
            e.printStackTrace();
        }
    }

    public static void generateRecapInvoicePDF(
            String number, String description, String clientName, String clientNIF, String clientAddress,
            Map<String, String> facturaImportes, String fullAmount, String qrBase64) {

        try {
            String desktopPath = System.getProperty("user.home") + "/Desktop/";
            Date now = new Date();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(now);
            String mes = new SimpleDateFormat("MMMM", new Locale("es", "ES")).format(now);
            String fileName = "Factura_Recapitulativa_" + number + "_" + date + ".pdf";

            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
            document.open();

            // ======== CABECERA ========
            Paragraph title = new Paragraph("FACTURA RECAPITULATIVA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Número: " + number));
            document.add(new Paragraph("Fecha de emisión: " + date));
            document.add(new Paragraph("Operaciones realizadas durante el mes de: " + mes));
            document.add(new Paragraph("Descripción general: " + description));
            document.add(Chunk.NEWLINE);

            // ======== DATOS DEL CLIENTE ========
            Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(clientHeader);
            document.add(new Paragraph("Nombre o Razón Social: " + clientName));
            document.add(new Paragraph("NIF: " + clientNIF));
            document.add(new Paragraph("Dirección: " + clientAddress));
            document.add(Chunk.NEWLINE);

            // ======== TABLA DE FACTURAS RESUMIDAS ========
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Stream.of("Número de factura", "Importe sin IVA")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                        table.addCell(header);
                    });

            double totalBase = 0.0;
            double tipoIVA = 21.0;

            for (Map.Entry<String, String> entry : facturaImportes.entrySet()) {
                String numero = entry.getKey();
                String importeStr = entry.getValue();
                double importeConIVA = Double.parseDouble(importeStr);
                double base = importeConIVA / (1 + tipoIVA / 100.0);
                base = Math.round(base * 100.0) / 100.0;
                totalBase += base;

                table.addCell(numero);
                table.addCell(String.format(Locale.US, "%.2f €", importeConIVA));
            }

            document.add(table);

            // ======== DESGLOSE DE IVA ========
            double cuotaIVA = totalBase * (tipoIVA / 100.0);
            double totalConIVA = totalBase + cuotaIVA;

            document.add(new Paragraph("Desglose del importe:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)));
            document.add(new Paragraph("Base imponible: " + String.format(Locale.US, "%.2f €", totalBase)));
            document.add(new Paragraph("IVA (" + (int) tipoIVA + "%): " + String.format(Locale.US, "%.2f €", cuotaIVA)));
            document.add(new Paragraph("Importe total (IVA incluido): " + String.format(Locale.US, "%.2f €", totalConIVA)));
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
            document.add(new Paragraph("Gracias por su confianza.",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

            document.close();
            System.out.println("PDF de factura recapitulativa guardado en: " + desktopPath + fileName);
        } catch (Exception ex) {
            System.out.print("Error al crear factura recapitulativa ");
            ex.printStackTrace();
        }
    }
}

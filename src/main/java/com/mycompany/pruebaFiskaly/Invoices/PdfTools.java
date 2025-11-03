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
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.json.JSONObject;

/**
 *
 * @author user
 */
public class PdfTools {

    public static void generateCompleteInvoicePDF(Map<String, String> receptorDetails, String number, List<JSONObject> itemsData, List<JSONObject> globalDiscounts, String fullAmount, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura_Completa_" + number + ".pdf";
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();
        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA COMPLETA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("N\u00famero: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(Chunk.NEWLINE);
        // ======== DATOS DEL CLIENTE ========
        Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        document.add(clientHeader);
        document.add(new Paragraph(receptorDetails.get("legal_name")));
        document.add(new Paragraph(receptorDetails.get("tax_number")));
        document.add(new Paragraph(receptorDetails.get("address_line")));
        document.add(Chunk.NEWLINE);

        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<JSONObject>> groupedByIVA = new HashMap<>();
        for (JSONObject item : itemsData) {
            String ivaRaw = item.optString("iva_rate", "21.0").trim();
            String ivaRate = ivaRaw.equalsIgnoreCase("exento") ? "0.0" : String.format(Locale.US, "%.1f", Double.parseDouble(ivaRaw));
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21.0", "10.0", "4.0", "0.0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<JSONObject> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5.0F);
            table.setSpacingAfter(5.0F);
            table.setWidths(new float[]{4.0F, 2.0F, 2.0F, 1.0F, 2.0F});

            Stream.of("Descripci\u00f3n", "Precio unitario", "Descuento", "Unidades", "Base imponible").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                table.addCell(header);
            });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (JSONObject item : group) {
                String text = item.optString("text", "");
                double unit = Double.parseDouble(item.optString("unit_amount", "0"));
                double qty = Double.parseDouble(item.optString("quantity", "0"));
                double dct = Double.parseDouble(item.optString("discount", "0"));
                double base = (unit - dct) * qty;
                double iva = Double.parseDouble(ivaRate);
                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(new PdfPCell(new Phrase(text)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", unit))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", dct))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(qty))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", base))));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph(
                    "Total base: " + String.format(Locale.US, "%.2f \u20ac", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f \u20ac", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TABLA DE DESCUENTOS GLOBALES (si existen) ========
        double descuentosGlobalesTotal = 0.0;
        if (globalDiscounts != null && !globalDiscounts.isEmpty()) {
            document.add(new Paragraph("Descuentos globales", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable descTable = new PdfPTable(5);
            descTable.setWidthPercentage(100);
            descTable.setSpacingBefore(5.0F);
            descTable.setSpacingAfter(5.0F);
            descTable.setWidths(new float[]{4.0F, 2.0F, 1.0F, 2.0F, 1.5F});
            Stream.of("Descripci\u00f3n", "Precio unitario", "Unidades", "IVA", "Importe").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                descTable.addCell(header);
            });
            for (JSONObject descuento : globalDiscounts) {
                String text = descuento.optString("text", "Descuento");
                String unitAmount = descuento.optString("unit_amount", "0");
                String quantity = descuento.optString("quantity", "1");
                String fullAmountStr = descuento.optString("full_amount", "0").trim().replace(",", ".");
                double fullAmountVal = 0.0;
                try {
                    fullAmountVal = Double.parseDouble(fullAmountStr);
                } catch (NumberFormatException ex) {
                    System.err.println("full_amount inv\u00e1lido en descuento: '" + fullAmountStr + "'. Usando 0.00");
                }
                descuentosGlobalesTotal += fullAmountVal;
                String ivaRate = "\u2014";
                try {
                    ivaRate = descuento.getJSONObject("system").getJSONObject("category").optString("rate", "0.0") + "%";
                } catch (Exception e) {
                    ivaRate = "0.0%";
                }
                descTable.addCell(new PdfPCell(new Phrase(text)));
                descTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", Double.parseDouble(unitAmount)))));
                descTable.addCell(new PdfPCell(new Phrase(quantity)));
                descTable.addCell(new PdfPCell(new Phrase(ivaRate)));
                descTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", fullAmountVal))));
            }
            document.add(descTable);
            Paragraph descSummary = new Paragraph("Total descuentos globales: " + String.format(Locale.US, "%.2f \u20ac", descuentosGlobalesTotal), FontFactory.getFont(FontFactory.HELVETICA, 11));
            descSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(descSummary);
            document.add(Chunk.NEWLINE);
        }
        // ======== TOTAL GENERAL INCLUYENDO SUPLIDOS Y DESCUENTOS ========
        double finalTotal = totalConIVA + descuentosGlobalesTotal; //suma el descuento ya que viene en negativo
        Paragraph total = new Paragraph("Importe total (IVA incluido, con suplidos y descuentos): " + String.format(Locale.US, "%.2f \u20ac", finalTotal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        document.add(Chunk.NEWLINE);
        // ======== QR FISCAL ========
        byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.scaleToFit(120, 120);
        qrImage.setAlignment(Image.ALIGN_RIGHT);
        document.add(new Paragraph("\nC\u00f3digo QR fiscal:"));
        document.add(qrImage);
        // ======== PIE DE PÁGINA ========
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Factura emitida electr\u00f3nicamente conforme a la normativa Verifactu.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));
        document.add(new Paragraph("Gracias por su confianza.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));
        document.close();
        System.out.println("PDF de factura completa guardado en: " + desktopPath + fileName);
    }

    public static void generateCorrectingInvoicePDF(String original_invoice_number, Map<String, String> receptorDetails, String number, List<JSONObject> itemsData, List<JSONObject> globalDiscounts, String fullAmountTotal, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura_Rectificativa_" + number + ".pdf";
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();
        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA RECTIFICATIVA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("N\u00famero: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(new Paragraph("Sustituye a factura " + original_invoice_number));
        document.add(Chunk.NEWLINE);
        // ======== DATOS DEL CLIENTE ========
        Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        document.add(clientHeader);
        document.add(new Paragraph(receptorDetails.get("legal_name")));
        document.add(new Paragraph(receptorDetails.get("tax_number")));
        document.add(new Paragraph(receptorDetails.get("address_line")));
        document.add(Chunk.NEWLINE);
        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<JSONObject>> groupedByIVA = new HashMap<>();
        for (JSONObject item : itemsData) {
            String ivaRaw = item.optString("iva_rate", "21.0").trim();
            String ivaRate = ivaRaw.equalsIgnoreCase("exento") ? "0.0" : String.format(Locale.US, "%.1f", Double.parseDouble(ivaRaw));
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21.0", "10.0", "4.0", "0.0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<JSONObject> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5.0F);
            table.setSpacingAfter(5.0F);
            table.setWidths(new float[]{4.0F, 2.0F, 2.0F, 1.0F, 2.0F});

            Stream.of("Descripci\u00f3n", "Precio unitario", "Descuento", "Unidades", "Base imponible").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                table.addCell(header);
            });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (JSONObject item : group) {
                String text = item.optString("text", "");
                double unit = Double.parseDouble(item.optString("unit_amount", "0"));
                double qty = Double.parseDouble(item.optString("quantity", "0"));
                double dct = Double.parseDouble(item.optString("discount", "0"));
                double base = (unit - dct) * qty;
                double iva = Double.parseDouble(ivaRate);
                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(new PdfPCell(new Phrase(text)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", unit))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", dct))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(qty))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", base))));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph(
                    "Total base: " + String.format(Locale.US, "%.2f \u20ac", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f \u20ac", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }
        
        // ======== TABLA DE DESCUENTOS GLOBALES (si existen) ========
        double descuentosGlobalesTotal = 0.0;
        if (globalDiscounts != null && !globalDiscounts.isEmpty()) {
            document.add(new Paragraph("Descuentos globales", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable descTable = new PdfPTable(5);
            descTable.setWidthPercentage(100);
            descTable.setSpacingBefore(5.0F);
            descTable.setSpacingAfter(5.0F);
            descTable.setWidths(new float[]{4.0F, 2.0F, 1.0F, 2.0F, 1.5F});
            Stream.of("Descripci\u00f3n", "Precio unitario", "Unidades", "IVA", "Importe").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                descTable.addCell(header);
            });
            for (JSONObject descuento : globalDiscounts) {
                String text = descuento.optString("text", "Descuento");
                String unitAmount = descuento.optString("unit_amount", "0");
                String quantity = descuento.optString("quantity", "1");
                String fullAmountStr = descuento.optString("full_amount", "0").trim().replace(",", ".");
                double fullAmountVal = 0.0;
                try {
                    fullAmountVal = Double.parseDouble(fullAmountStr);
                } catch (NumberFormatException ex) {
                    System.err.println("full_amount inv\u00e1lido en descuento: '" + fullAmountStr + "'. Usando 0.00");
                }
                descuentosGlobalesTotal += fullAmountVal;
                String ivaRate = "\u2014";
                try {
                    ivaRate = descuento.getJSONObject("system").getJSONObject("category").optString("rate", "0.0") + "%";
                } catch (Exception e) {
                    ivaRate = "0.0%";
                }
                descTable.addCell(new PdfPCell(new Phrase(text)));
                descTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", Double.parseDouble(unitAmount)))));
                descTable.addCell(new PdfPCell(new Phrase(quantity)));
                descTable.addCell(new PdfPCell(new Phrase(ivaRate)));
                descTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f \u20ac", fullAmountVal))));
            }
            document.add(descTable);
            Paragraph descSummary = new Paragraph("Total descuentos globales: " + String.format(Locale.US, "%.2f \u20ac", descuentosGlobalesTotal), FontFactory.getFont(FontFactory.HELVETICA, 11));
            descSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(descSummary);
            document.add(Chunk.NEWLINE);
        }
        // ======== TOTAL GENERAL INCLUYENDO SUPLIDOS Y DESCUENTOS ========
        double finalTotal = totalConIVA + descuentosGlobalesTotal; //suma el descuento ya que viene en negativo
        Paragraph total = new Paragraph("Importe total (IVA incluido, con suplidos y descuentos): " + String.format(Locale.US, "%.2f \u20ac", finalTotal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        document.add(Chunk.NEWLINE);
        // ======== QR FISCAL ========
        byte[] qrBytes = Base64.getDecoder().decode(qrBase64);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.scaleToFit(120, 120);
        qrImage.setAlignment(Image.ALIGN_RIGHT);
        document.add(new Paragraph("\nC\u00f3digo QR fiscal:"));
        document.add(qrImage);
        // ======== PIE DE PÁGINA ========
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Factura emitida electr\u00f3nicamente conforme a la normativa Verifactu.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));
        document.add(new Paragraph("Gracias por su confianza.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));
        document.close();
        System.out.println("PDF de factura rectificativa guardado en: " + desktopPath + fileName);
    }
}

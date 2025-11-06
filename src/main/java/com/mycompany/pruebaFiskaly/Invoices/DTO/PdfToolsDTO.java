package com.mycompany.pruebaFiskaly.Invoices.DTO;

import com.mycompany.pruebaFiskaly.Invoices.InvoicesManagement;
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
import org.json.JSONException;
import org.json.JSONObject;

public class PdfToolsDTO {

    public static String setQR(String responseBody) throws JSONException {
        JSONObject json = new JSONObject(responseBody).getJSONObject("content");
        JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
        String qrBase64 = qr.getString("data");
        return qrBase64;
    }

    public static void generateCompleteInvoicePDF(CompleteDTO complete, String qrBase64) throws Exception {
        // ======== CONFIGURACIÓN BÁSICA ========
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String number = complete.content.data.invoiceNumber;
        String fileName = "Factura_Completa_" + number + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA COMPLETA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Número: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(Chunk.NEWLINE);

        // ======== DATOS DEL CLIENTE ========
        if (complete.content.recipients != null && !complete.content.recipients.isEmpty()) {
            RecipientsDTO recipient = complete.content.recipients.get(0);
            Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(clientHeader);
            document.add(new Paragraph(recipient.id.legalName));
            document.add(new Paragraph("NIF: " + recipient.id.taxNumber));
            document.add(new Paragraph(recipient.addressLine + " - " + recipient.postalCode));
            document.add(Chunk.NEWLINE);
        }

        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<ItemDTO>> groupedByIVA = new HashMap<>();
        for (ItemDTO item : complete.content.data.items) {
            String ivaRate = "0";
            try {
                if (item.system != null && item.system.category != null && item.system.category.rate != null) {
                    ivaRate = item.system.category.rate.toString();
                } else {
                    ivaRate = "0"; // Exento o sin IVA
                }

            } catch (Exception e) {
                ivaRate = "0";
            }
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21", "10", "4", "0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<ItemDTO> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 1, 2});

            Stream.of("Descripción", "Precio unitario", "Descuento", "Unidades", "Base imponible").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                table.addCell(header);
            });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (ItemDTO item : group) {
                double unit = safeParseDouble(item.unitAmount);
                double qty = safeParseDouble(item.quantity);
                double dct = safeParseDouble(item.discount);
                double base = (unit - dct) * qty;

                double iva = 0.0;
                try {
                    iva = (ivaRate != null && !ivaRate.isEmpty()) ? Double.parseDouble(ivaRate) : 0.0;
                } catch (NumberFormatException e) {
                    iva = 0.0;
                }

                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(new PdfPCell(new Phrase(item.text)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", unit))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", dct))));
                table.addCell(new PdfPCell(new Phrase(item.quantity)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", base))));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph(
                    "Total base: " + String.format(Locale.US, "%.2f €", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f €", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TOTAL FACTURA ========
        double fullAmount = safeParseDouble(complete.content.data.fullAmount);
        Paragraph total = new Paragraph(
                "Importe total (IVA incluido): " + String.format(Locale.US, "%.2f €", fullAmount),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)
        );
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
        document.add(new Paragraph("Gracias por su confianza.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

        document.close();
        System.out.println("✅ PDF de factura completa guardado en: " + desktopPath + fileName);
    }

    public static void generateCorrectingInvoicePDF(CorrectingDTO correcting, String qrBase64) throws Exception {
        // ======== CONFIGURACIÓN BÁSICA ========
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String number = correcting.content.invoice.data.number;
        String fileName = "Factura_Rectificativa_" + number + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA RECTIFICATIVA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Número: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(new Paragraph("Rectifica a factura : " + InvoicesManagement.getInvoiceNumberByID(correcting.content.getId())));
        document.add(Chunk.NEWLINE);

        // ======== DATOS DEL CLIENTE ========
        if (correcting.content.invoice.recipients != null && !correcting.content.invoice.recipients.isEmpty()) {
            RecipientsDTO recipient = correcting.content.invoice.recipients.get(0);
            Paragraph clientHeader = new Paragraph("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            document.add(clientHeader);
            document.add(new Paragraph(recipient.id.legalName));
            document.add(new Paragraph("NIF: " + recipient.id.taxNumber));
            document.add(new Paragraph(recipient.addressLine + " - " + recipient.postalCode));
            document.add(Chunk.NEWLINE);
        }

        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<ItemDTO>> groupedByIVA = new HashMap<>();
        for (ItemDTO item : correcting.content.invoice.data.items) {
            String ivaRate = "0";
            try {
                if (item.system != null && item.system.category != null && item.system.category.rate != null) {
                    ivaRate = item.system.category.rate.toString();
                } else {
                    ivaRate = "0"; // Exento o sin IVA
                }

            } catch (Exception e) {
                ivaRate = "0";
            }
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21", "10", "4", "0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<ItemDTO> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 1, 2});

            Stream.of("Descripción", "Precio unitario", "Descuento", "Unidades", "Base imponible").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                table.addCell(header);
            });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (ItemDTO item : group) {
                double unit = safeParseDouble(item.unitAmount);
                double qty = safeParseDouble(item.quantity);
                double dct = safeParseDouble(item.discount);
                double base = (unit - dct) * qty;

                double iva = 0.0;
                try {
                    iva = (ivaRate != null && !ivaRate.isEmpty()) ? Double.parseDouble(ivaRate) : 0.0;
                } catch (NumberFormatException e) {
                    iva = 0.0;
                }

                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(new PdfPCell(new Phrase(item.text)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", unit))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", dct))));
                table.addCell(new PdfPCell(new Phrase(item.quantity)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", base))));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph(
                    "Total base: " + String.format(Locale.US, "%.2f €", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f €", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TOTAL FACTURA ========
        double fullAmount = safeParseDouble(correcting.content.invoice.data.fullAmount);
        Paragraph total = new Paragraph(
                "Importe total (IVA incluido): " + String.format(Locale.US, "%.2f €", fullAmount),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)
        );
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
        document.add(new Paragraph("Gracias por su confianza.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

        document.close();
        System.out.println("✅ PDF de factura completa guardado en: " + desktopPath + fileName);
    }

    /**
     * Método auxiliar seguro para convertir Strings a double sin errores.
     */
    private static double safeParseDouble(String value) {
        if (value == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}

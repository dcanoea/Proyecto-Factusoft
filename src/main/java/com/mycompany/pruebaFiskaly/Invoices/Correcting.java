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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
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

public class Correcting {

    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA COMPLETA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoiceSubstitutionComplete(int originalInvoiceNumberInt, int newInvoiceNumber, List<Map<String, String>> itemsList, List<JSONObject> suppliedItems, List<JSONObject> globalDiscounts, Map<String, String> receptorDetails) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String clientID = Clients.getFirstClientID();
            String token = Authentication.retrieveToken();
            UUID invoiceID = UUID.randomUUID();
            String invoiceNumber = String.valueOf(newInvoiceNumber);
            String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
            
            String originalInvoiceNumber = String.valueOf(originalInvoiceNumberInt);
            String originalInvoiceID = InvoicesManagement.getInvoiceIDByNumber(originalInvoiceNumber);

            String invoiceSeries = "R2025";// Obligatorio en facturas rectificativas

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DE LÍNEAS DE LA FACTURA =========
            JSONArray items = new JSONArray();
            double totalAmount = 0.0;

            for (Map<String, String> itemData : itemsList) {
                String text = itemData.get("text");
                String quantity = itemData.get("quantity");
                String unitAmount = itemData.get("unit_amount");
                String discount = itemData.get("discount");
                String ivaRate = itemData.get("iva_rate");

                double unit = Double.parseDouble(unitAmount);
                double qty = Double.parseDouble(quantity);
                double dct = Double.parseDouble(discount);

                double iva;
                double full;
                double base = (unit - dct) * qty;

                if (ivaRate.equalsIgnoreCase("exento")) {
                    iva = 0.0;
                    full = base;
                } else {
                    iva = Double.parseDouble(ivaRate);
                    full = base * (1 + iva / 100);
                }
                totalAmount += full;
                String fullAmount = String.format(Locale.US, "%.2f", full);

                JSONObject category = new JSONObject();
                if (ivaRate.equalsIgnoreCase("exento")) {
                    category.put("type", "NO_VAT");
                    category.put("cause", "TAXABLE_EXEMPT_1");
                } else {
                    category.put("type", "VAT");
                    category.put("rate", ivaRate);
                }

                JSONObject system = new JSONObject();
                system.put("type", "REGULAR");
                system.put("category", category);

                JSONObject item = new JSONObject();
                item.put("text", text);
                item.put("quantity", quantity);
                item.put("unit_amount", unitAmount);
                item.put("discount", discount);
                item.put("full_amount", fullAmount);
                item.put("system", system);

                items.put(item);
            }

            // Ítems suplidos (NO se suman a la base pero si al total de la factura)
            for (JSONObject suplido : suppliedItems) {
                items.put(suplido);
                String suplidoFullStr = "0";
                if (suplido.has("full_amount")) {
                    Object fa = suplido.get("full_amount");
                    if (fa != null) {
                        suplidoFullStr = String.valueOf(fa);
                    }
                }
                suplidoFullStr = suplidoFullStr.trim().replace(",", ".");
                double suplidoFull = 0.0;
                try {
                    suplidoFull = Double.parseDouble(suplidoFullStr);
                } catch (NumberFormatException e) {
                    System.err.println("full_amount inválido en suplido: '" + suplidoFullStr + "'. Asumiendo 0.00");
                }
                totalAmount += suplidoFull;
            }

            // Descuentos globales
            for (JSONObject discount : globalDiscounts) {
                items.put(discount);
                String discountFullStr = "0";
                if (discount.has("full_amount")) {
                    Object fa = discount.get("full_amount");
                    if (fa != null) {
                        discountFullStr = String.valueOf(fa);
                    }
                }
                discountFullStr = discountFullStr.trim().replace(",", ".");
                double discountFull = 0.0;
                try {
                    discountFull = Double.parseDouble(discountFullStr);
                } catch (NumberFormatException e) {
                    System.err.println("full_amount inválido en suplido: '" + discountFullStr + "'. Asumiendo 0.00");
                }
                totalAmount += discountFull;
            }

            String fullAmountTotal = String.format(Locale.US, "%.2f", totalAmount);

            // ======== RECEPTOR =========
            JSONObject id = new JSONObject();
            id.put("legal_name", receptorDetails.get("legal_name"));
            id.put("tax_number", receptorDetails.get("tax_number"));
            id.put("registered", Boolean.parseBoolean(receptorDetails.get("registered")));// BOOLEAN

            JSONObject recipient = new JSONObject();
            recipient.put("id", id);
            recipient.put("address_line", receptorDetails.get("address_line"));
            recipient.put("postal_code", receptorDetails.get("postal_code"));

            JSONArray recipients = new JSONArray();
            recipients.put(recipient);

            // ======== SUBOBJETO DATA =========
            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED"); // Este campo debe ser SIMPLIFIED incluso en factura COMPLETE
            data.put("number", invoiceNumber);
            data.put("series", invoiceSeries);
            data.put("text", "Factura RECTIFICATIVA");
            data.put("items", items);
            data.put("full_amount", fullAmountTotal);

            // ======== OBJETO INVOICE =========
            JSONObject invoice = new JSONObject();
            invoice.put("type", "COMPLETE");
            invoice.put("data", data);
            invoice.put("recipients", recipients);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "SUBSTITUTION");
            content.put("id", originalInvoiceID); // UUIDv4 válido de la factura original. Se obtiene mediante método Invoices_Management.get_Invoice_Id(numeroFactura);
            content.put("invoice", invoice);
            content.put("code", "CORRECTION_1");

            JSONObject body = new JSONObject();
            body.put("content", content);

            put.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

            // ======== PETICIÓN HTTP =========
            HttpResponse response = client.execute(put);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            System.out.println("Código de respuesta: " + statusCode);
            System.out.println(responseBody.toString());

            JSONObject json = new JSONObject(responseBody).getJSONObject("content");
            JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
            String qrBase64 = qr.getString("data");

            generateCorrectingInvoicePDF(originalInvoiceNumber, receptorDetails, invoiceNumber, itemsList, suppliedItems, globalDiscounts, fullAmountTotal, qrBase64);

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }

    public static void createItem(List<Map<String, String>> itemsList, String text, String quantity, String discount, String unitAmount, String ivaRate) {
        // Validar que el IVA está entre los permitidos
        List<String> validIvaRates = Arrays.asList(
                Config.IVA_GENERAL,
                Config.IVA_REDUCIDO,
                Config.IVA_SUPERREDUCIDO,
                Config.IVA_EXENTO
        );

        if (!validIvaRates.contains(ivaRate)) {
            throw new IllegalArgumentException("IVA no válido: " + ivaRate + ". Debe ser uno de: " + validIvaRates);
        }

        Map<String, String> item = new HashMap<>();
        if (ivaRate.equals(Config.IVA_EXENTO)) {
            item.put("text", text + " (Exento según art. 20 Ley 37/1992 del IVA)");
            item.put("iva_rate", "exento");
        } else {
            item.put("text", text);
            item.put("iva_rate", ivaRate);
        }
        item.put("quantity", quantity);
        item.put("discount", discount);
        item.put("unit_amount", unitAmount);

        itemsList.add(item);
    }

    public static JSONObject createSupplied(String text, String quantity, String unitAmount, String fullAmount) {
        JSONObject category = new JSONObject();
        category.put("type", "NO_VAT");
        category.put("cause", "NON_TAXABLE_4");

        JSONObject system = new JSONObject();
        system.put("category", category);
        system.put("type", "REGULAR");

        JSONObject item = new JSONObject();
        item.put("text", text);
        item.put("quantity", quantity);
        item.put("unit_amount", unitAmount);
        item.put("full_amount", fullAmount);
        item.put("system", system);
        //item.put("vat_type", "IVA");

        return item;
    }

    public static JSONObject createGlobalDiscount(String iva, String quantity, String unitAmount) {
        double unit = Double.parseDouble(unitAmount);
        double qty = Double.parseDouble(quantity);
        double IVA;
        if (iva.equals(Config.IVA_EXENTO)) {
            IVA = 0.00;
        } else {
            IVA = Double.parseDouble(iva);
        }
        double fullAmount = (unit * qty) * (1 + (IVA / 100));
        String fullAmountTotal = String.format(Locale.US, "%.2f", fullAmount);

        JSONObject category = new JSONObject();
        if (iva.equals(Config.IVA_EXENTO)) {
            category.put("type", "NO_VAT");
            category.put("cause", "TAXABLE_EXEMPT_1");
        } else {
            category.put("type", "VAT");
            category.put("rate", iva);
        }

        JSONObject system = new JSONObject();
        system.put("type", "REGULAR");
        system.put("category", category);

        JSONObject item = new JSONObject();
        item.put("text", "Descuento");
        item.put("quantity", quantity);
        item.put("unit_amount", unitAmount);
        item.put("full_amount", fullAmountTotal);
        item.put("system", system);

        return item;
    }

    public static Map<String, String> createReceptor(String legal_name, String tax_number, boolean registered, String address_line, String postal_code) {
        Map<String, String> receptorDetails = new HashMap<>();
        receptorDetails.put("legal_name", legal_name);
        receptorDetails.put("tax_number", tax_number);
        receptorDetails.put("registered", String.valueOf(registered));//BOOLEANO
        receptorDetails.put("address_line", address_line);
        receptorDetails.put("postal_code", postal_code);
        return receptorDetails;
    }

    public static void generateCorrectingInvoicePDF(String original_invoice_number, Map<String, String> receptorDetails, String number, List<Map<String, String>> itemsData, List<JSONObject> suppliedItems, List<JSONObject> globalDiscounts, String fullAmount, String qrBase64) throws Exception {
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
        document.add(new Paragraph("Número: " + number));
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
        Map<String, List<Map<String, String>>> groupedByIVA = new HashMap<>();
        for (Map<String, String> item : itemsData) {
            String ivaRaw = item.getOrDefault("iva_rate", "21.0").trim();
            String ivaRate = ivaRaw.equalsIgnoreCase("exento") ? "0.0" : String.format(Locale.US, "%.1f", Double.parseDouble(ivaRaw));
            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList("21.0", "10.0", "4.0", "0.0");
        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<Map<String, String>> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);
            table.setWidths(new float[]{4f, 2f, 2f, 1f, 2f});

            Stream.of("Descripción", "Precio unitario", "Descuento", "Unidades", "Base imponible").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                table.addCell(header);
            });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (Map<String, String> item : group) {
                String text = item.get("text");
                double unit = Double.parseDouble(item.get("unit_amount"));
                double qty = Double.parseDouble(item.get("quantity"));
                double dct = Double.parseDouble(item.get("discount"));
                double base = (unit - dct) * qty;
                double iva = Double.parseDouble(ivaRate);
                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(new PdfPCell(new Phrase(text)));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", unit))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", dct))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(qty))));
                table.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", base))));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph("Total base: " + String.format(Locale.US, "%.2f €", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f €", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TABLA DE SUPLIDOS ========
        double suplidosTotal = 0.0;
        if (suppliedItems != null && !suppliedItems.isEmpty()) {
            document.add(new Paragraph("Suplidos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable supTable = new PdfPTable(4);
            supTable.setWidthPercentage(100);
            supTable.setSpacingBefore(5f);
            supTable.setSpacingAfter(5f);
            supTable.setWidths(new float[]{5f, 2f, 1f, 2f});

            Stream.of("Descripción", "Precio unitario", "Unidades", "Importe").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                header.setBorderWidth(1);
                supTable.addCell(header);
            });

            for (JSONObject suplido : suppliedItems) {
                String text = suplido.optString("text", "");
                String unitAmount = suplido.optString("unit_amount", "0");
                String quantity = suplido.optString("quantity", "1");
                String fullAmountStr = suplido.optString("full_amount", "0").trim().replace(",", ".");
                double fullAmountVal = 0.0;
                try {
                    fullAmountVal = Double.parseDouble(fullAmountStr);
                } catch (NumberFormatException ex) {
                    System.err.println("full_amount inválido en suplido: '" + fullAmountStr + "'. Usando 0.00");
                }

                suplidosTotal += fullAmountVal;

                supTable.addCell(new PdfPCell(new Phrase(text)));
                supTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", Double.parseDouble(unitAmount)))));
                supTable.addCell(new PdfPCell(new Phrase(quantity)));
                supTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", fullAmountVal))));
            }

            document.add(supTable);

            Paragraph supSummary = new Paragraph("Total suplidos: " + String.format(Locale.US, "%.2f €", suplidosTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            supSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(supSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TABLA DE DESCUENTOS GLOBALES (si existen) ========
        double descuentosGlobalesTotal = 0.0;
        if (globalDiscounts != null && !globalDiscounts.isEmpty()) {
            document.add(new Paragraph("Descuentos globales", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable descTable = new PdfPTable(5);
            descTable.setWidthPercentage(100);
            descTable.setSpacingBefore(5f);
            descTable.setSpacingAfter(5f);
            descTable.setWidths(new float[]{4f, 2f, 1f, 2f, 1.5f});

            Stream.of("Descripción", "Precio unitario", "Unidades", "IVA", "Importe").forEach(headerTitle -> {
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
                    System.err.println("full_amount inválido en descuento: '" + fullAmountStr + "'. Usando 0.00");
                }

                descuentosGlobalesTotal += fullAmountVal;

                String ivaRate = "—";
                try {
                    ivaRate = descuento.getJSONObject("system").getJSONObject("category").optString("rate", "0.0") + "%";
                } catch (Exception e) {
                    ivaRate = "0.0%";
                }

                descTable.addCell(new PdfPCell(new Phrase(text)));
                descTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", Double.parseDouble(unitAmount)))));
                descTable.addCell(new PdfPCell(new Phrase(quantity)));
                descTable.addCell(new PdfPCell(new Phrase(ivaRate)));
                descTable.addCell(new PdfPCell(new Phrase(String.format(Locale.US, "%.2f €", fullAmountVal))));

            }

            document.add(descTable);

            Paragraph descSummary = new Paragraph("Total descuentos globales: " + String.format(Locale.US, "%.2f €", descuentosGlobalesTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            descSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(descSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TOTAL GENERAL INCLUYENDO SUPLIDOS Y DESCUENTOS ========
        double finalTotal = totalConIVA + suplidosTotal + descuentosGlobalesTotal; //suma el descuento ya que viene en negativo
        Paragraph total = new Paragraph("Importe total (IVA incluido, con suplidos y descuentos): " + String.format(Locale.US, "%.2f €", finalTotal),
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
        document.add(new Paragraph("Gracias por su confianza.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY)));

        document.close();
        System.out.println("PDF de factura rectificativa guardado en: " + desktopPath + fileName);
    }

    /*
        //MÉTODO PARA CREAR FACTURAS RECTIFICATIVAS POR DIFERENCIA PARA FACTURAS COMPLETAS
    public static void createCorrectingInvoiceDifferencesComplete(String originalInvoiceNumber, String originalInvoiceID) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String clientID = Clients.getFirstClientID();
            UUID invoiceID = UUID.randomUUID();
            String invoiceNumber = originalInvoiceNumber + "R";
            if (invoiceNumber.length() > 20) {
                invoiceNumber = invoiceNumber.substring(0, 20);
            }
            String invoiceSeries = "R-2025";
            String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DEL ÍTEM =========
            String text = "Ajuste por diferencia de precio";
            String quantity = "1.00";
            String unitAmount = "10.00";
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
            String fullAmount = String.format(Locale.US, "%.2f", full);

            JSONObject category = new JSONObject();
            category.put("type", "VAT");
            category.put("rate", ivaRate);

            JSONObject system = new JSONObject();
            system.put("type", "REGULAR");
            system.put("category", category);

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

            // ======== SUBOBJETO DATA =========
            JSONObject data = new JSONObject();
            data.put("type", "SIMPLIFIED");
            data.put("number", invoiceNumber);
            data.put("series", invoiceSeries);
            data.put("text", "Rectificación por diferencia");
            data.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            data.put("items", items);
            data.put("full_amount", fullAmount);

            // ======== OBJETO INVOICE =========
            JSONObject invoice = new JSONObject();
            invoice.put("type", "COMPLETE");
            invoice.put("data", data);
            invoice.put("recipients", recipients);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "DIFFERENCES");
            content.put("code", "CORRECTION_1");
            content.put("id", originalInvoiceID);
            content.put("invoice", invoice);

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
            System.out.println(jsonPrint.toString(2));

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject json = new JSONObject(responseBody).getJSONObject("content");
                JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
                String qrBase64 = qr.getString("data");

                generateCorrectingInvoicePDF(invoiceNumber, "DIFFERENCES", "ARAGON FORMACION ACF S.L.",
                        "B22260863", "Calle Mayor 123, Huesca", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
            } else {
                System.err.println("Error al crear la factura rectificativa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }
     */
 /*
    // FACTURA RECTIFICATIVA DE SUSTITUCIÓN DE FACTURA SIMPLIFICADA(reemplaza completamente a la factura original)
    public static void createCorrectingInvoiceSubstitutionSimplified(String originalInvoiceNumber, String originalInvoiceID) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String clientID = Clients.getFirstClientID();
            UUID invoiceID = UUID.randomUUID();
            String invoiceNumber = originalInvoiceNumber + "R"; // Máximo 20 caracteres
            String invoiceSeries = "R-2025"; // Obligatorio en facturas rectificativas
            String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            // ======== DATOS DEL ÍTEM =========
            String text = "Curso ADR";
            String quantity = "2.00";
            String unitAmount = "210.74";
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
            String fullAmount = String.format(Locale.US, "%.2f", full);

            JSONObject category = new JSONObject();
            category.put("type", "VAT");
            category.put("rate", ivaRate);

            JSONObject system = new JSONObject();
            system.put("type", "REGULAR");
            system.put("category", category);

            JSONObject item = new JSONObject();
            item.put("text", text);
            item.put("quantity", quantity);
            item.put("unit_amount", unitAmount);
            item.put("full_amount", fullAmount);
            item.put("system", system);

            JSONArray items = new JSONArray();
            items.put(item);

            // ======== OBJETO INVOICE SIMPLIFICADO =========
            JSONObject invoice = new JSONObject();
            invoice.put("type", "SIMPLIFIED");
            invoice.put("number", invoiceNumber);
            invoice.put("series", invoiceSeries);
            invoice.put("text", "Factura RECTIFICATIVA");
            invoice.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            invoice.put("items", items);
            invoice.put("full_amount", fullAmount);

            // ======== CONTENIDO PRINCIPAL =========
            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "SUBSTITUTION");
            content.put("code", "CORRECTION_1");
            content.put("id", originalInvoiceID); // UUIDv4 válido de la factura original
            content.put("invoice", invoice);

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
            System.out.println(jsonPrint.toString(2));

            JSONObject json = new JSONObject(responseBody).getJSONObject("content");
            JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
            String qrBase64 = qr.getString("data");

            generateCorrectingInvoicePDF(invoiceNumber, "Factura RECTIFICATIVA", "CLIENTE SIMPLIFICADO",
                    "N/A", "N/A", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }*/
 /*
    //FACTURA RECTIFICATIVA DE DIFERENCIA DE FACTURA SIMPLIFICADA
    public static void createCorrectingInvoiceDifferencesSimplified(String originalInvoiceNumber, String originalInvoiceID) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String clientID = Clients.getFirstClientID();
            UUID invoiceID = UUID.randomUUID();
            String invoiceNumber = originalInvoiceNumber + "R";
            String invoiceSeries = "R-2025"; // Serie para rectificativas
            String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            String text = "Ajuste por diferencia de precio";
            String quantity = "1.00";
            String unitAmount = "10.00"; // solo el ajuste, no el total original
            String ivaRate = "21.0";

            double unit = Double.parseDouble(unitAmount);
            double qty = Double.parseDouble(quantity);
            double iva = Double.parseDouble(ivaRate);
            double full = unit * qty * (1 + iva / 100);
            String fullAmount = String.format(Locale.US, "%.2f", full);

            JSONObject category = new JSONObject();
            category.put("type", "VAT");
            category.put("rate", ivaRate);

            JSONObject system = new JSONObject();
            system.put("type", "REGULAR");
            system.put("category", category);

            JSONObject item = new JSONObject();
            item.put("text", text);
            item.put("quantity", quantity);
            item.put("unit_amount", unitAmount);
            item.put("full_amount", fullAmount);
            item.put("system", system);

            JSONArray items = new JSONArray();
            items.put(item);

            JSONObject invoice = new JSONObject();
            invoice.put("type", "SIMPLIFIED");
            invoice.put("number", invoiceNumber);
            invoice.put("series", invoiceSeries);
            invoice.put("text", "Rectificación por diferencia");
            invoice.put("issued_at", ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            invoice.put("items", items);
            invoice.put("full_amount", fullAmount);

            JSONObject content = new JSONObject();
            content.put("type", "CORRECTING");
            content.put("method", "DIFFERENCES");
            content.put("code", "CORRECTION_1");
            content.put("id", originalInvoiceID); // UUID de la factura original
            content.put("invoice", invoice);

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
            System.out.println(jsonPrint.toString(2));

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject json = new JSONObject(responseBody).getJSONObject("content");
                JSONObject qr = json.getJSONObject("compliance").getJSONObject("code").getJSONObject("image");
                String qrBase64 = qr.getString("data");

                generateCorrectingInvoicePDF(invoiceNumber, "Factura RECTIFICATIVA (DIFERENCIA)", "CLIENTE SIMPLIFICADO",
                        "N/A", "N/A", text, quantity, unitAmount, ivaRate, fullAmount, qrBase64);
            } else {
                System.err.println("Error al crear la factura rectificativa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura rectificativa");
            e.printStackTrace();
        }
    }*/
}

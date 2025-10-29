package com.mycompany.pruebaFiskaly.Invoices;

import java.util.Arrays;

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
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Summary {

    public static void createSummaryInvoice(int invoiceNumber, List<String> numerosFactura) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String clientID = Clients.getFirstClientID();
            UUID invoiceID = UUID.randomUUID();
            String invoiceNumStr = String.valueOf(invoiceNumber);
            String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + invoiceID;
            String token = Authentication.retrieveToken();

            HttpPut put = new HttpPut(url);
            put.setHeader("Content-Type", "application/json");
            put.setHeader("Authorization", "Bearer " + token);

            JSONArray items = new JSONArray();
            JSONArray summarizedInvoices = new JSONArray();
            Map<String, String> facturaImportes = new LinkedHashMap<>();
            double totalConIVA = 0.0;

            for (String numero : numerosFactura) {
                String uuid = InvoicesManagement.getInvoiceIDByNumber(numero);
                JSONArray facturaItems = Summary.getInvoiceItems(uuid); // usa método local
                summarizedInvoices.put(uuid);

                double totalFactura = 0.0;

                for (int i = 0; i < facturaItems.length(); i++) {
                    JSONObject originalItem = facturaItems.getJSONObject(i);
                    String text = originalItem.getString("text");
                    String quantity = originalItem.getString("quantity");
                    String unitAmount = originalItem.getString("unit_amount");

                    JSONObject system = originalItem.getJSONObject("system");
                    JSONObject category = system.getJSONObject("category");
                    String ivaRate = category.getString("rate");

                    double unit = Double.parseDouble(unitAmount);
                    double qty = Double.parseDouble(quantity);
                    double iva = Double.parseDouble(ivaRate);
                    double base = unit * qty;
                    double cuota = base * iva / 100;
                    double total = base + cuota;

                    totalFactura += total;
                    totalConIVA += total;

                    JSONObject item = new JSONObject();
                    item.put("text", "Factura " + numero + ": " + text);
                    item.put("quantity", quantity);
                    item.put("unit_amount", String.format(Locale.US, "%.2f", unit));
                    item.put("full_amount", String.format(Locale.US, "%.2f", total));
                    item.put("system", system);

                    items.put(item);
                }

                facturaImportes.put(numero, String.format(Locale.US, "%.2f", totalFactura));
            }

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
            data.put("number", String.valueOf(invoiceNumber));
            data.put("text", "Factura recapitulativa");
            data.put("items", items);
            data.put("full_amount", String.format(Locale.US, "%.2f", totalConIVA));

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

                List<Map<String, String>> itemsData = itemsDataFromJson(items);
                Summary.generateRecapPDF(invoiceNumStr, itemsData, numerosFactura, qrBase64);
            } else {
                System.err.println("Error al crear la factura recapitulativa (" + statusCode + ")");
            }

        } catch (Exception e) {
            System.err.println("Error al crear la factura recapitulativa");
            e.printStackTrace();
        }
    }

    private static List<Map<String, String>> itemsDataFromJson(JSONArray items) {
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            map.put("text", item.getString("text"));
            map.put("quantity", item.getString("quantity"));
            map.put("unit_amount", item.getString("unit_amount"));

            JSONObject system = item.getJSONObject("system");
            JSONObject category = system.getJSONObject("category");
            map.put("iva_rate", category.getString("rate"));

            result.add(map);
        }
        return result;
    }

    public static JSONArray getInvoiceItems(String uuid) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String clientID = Clients.getFirstClientID();
            String url = Config.BASE_URL + "/clients/" + clientID + "/invoices/" + uuid;
            String token = Authentication.retrieveToken();

            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + token);

            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode >= 200 && statusCode < 300) {
                JSONObject json = new JSONObject(responseBody);
                JSONObject content = json.getJSONObject("content");

                if (!content.has("data")) {
                    System.err.println("Factura sin campo 'data'");
                    return new JSONArray();
                }

                String dataStr = content.getString("data");
                JSONObject dataJson = new JSONObject(dataStr);

                if (!dataJson.has("items")) {
                    System.err.println("Factura sin ítems");
                    return new JSONArray();
                }

                return dataJson.getJSONArray("items");
            } else {
                System.err.println("Error al recuperar factura (" + statusCode + ")");
                return new JSONArray();
            }

        } catch (Exception e) {
            System.err.println("Error al obtener ítems de factura");
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void generateRecapPDF(String number, List<Map<String, String>> itemsData, List<String> facturasOriginales, String qrBase64) throws Exception {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "Factura Recapitulativa " + number + ".pdf";

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, new FileOutputStream(desktopPath + fileName));
        document.open();

        // ======== CABECERA ========
        Paragraph title = new Paragraph("FACTURA RECAPITULATIVA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("Número: " + number));
        document.add(new Paragraph("Fecha: " + date));
        document.add(Chunk.NEWLINE);

        // ======== TEXTO LEGAL ========
        String sustituidas = String.join(", ", facturasOriginales);
        Paragraph legal = new Paragraph("Sustituye a: " + sustituidas, FontFactory.getFont(FontFactory.HELVETICA, 11));
        legal.setAlignment(Element.ALIGN_LEFT);
        document.add(legal);
        document.add(Chunk.NEWLINE);

        // ======== AGRUPAR ÍTEMS POR IVA ========
        Map<String, List<Map<String, String>>> groupedByIVA = new HashMap<>();
        for (Map<String, String> item : itemsData) {
            String ivaRaw = item.getOrDefault("iva_rate", Config.IVA_GENERAL).trim();
            String ivaRate = String.format(Locale.US, "%.1f", Double.parseDouble(ivaRaw));

            groupedByIVA.computeIfAbsent(ivaRate, k -> new ArrayList<>()).add(item);
        }

        List<String> ivaOrder = Arrays.asList(Config.IVA_GENERAL, Config.IVA_REDUCIDO, Config.IVA_SUPERREDUCIDO, Config.IVA_EXENTO);

        double totalConIVA = 0.0;

        for (String ivaRate : ivaOrder) {
            List<Map<String, String>> group = groupedByIVA.get(ivaRate);
            if (group == null || group.isEmpty()) {
                continue;
            }

            document.add(new Paragraph("IVA " + ivaRate + "%", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5f);
            table.setSpacingAfter(5f);

            Stream.of("Descripción", "Precio unitario", "Unidades", "Base imponible")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
                        header.setBorderWidth(1);
                        table.addCell(header);
                    });

            double baseTotal = 0.0;
            double ivaTotal = 0.0;

            for (Map<String, String> item : group) {
                String text = item.get("text");
                String unitAmount = item.get("unit_amount");
                String quantity = item.get("quantity");

                double unit = Double.parseDouble(unitAmount);
                double qty = Double.parseDouble(quantity);
                double base = unit * qty;
                double iva = Double.parseDouble(ivaRate);
                double cuota = base * iva / 100;
                double total = base + cuota;

                baseTotal += base;
                ivaTotal += cuota;
                totalConIVA += total;

                table.addCell(text);
                table.addCell(String.format(Locale.US, "%.2f €", unit));
                table.addCell(quantity);
                table.addCell(String.format(Locale.US, "%.2f €", base));
            }

            document.add(table);

            Paragraph ivaSummary = new Paragraph("Total base: " + String.format(Locale.US, "%.2f €", baseTotal)
                    + "   IVA " + ivaRate + "%: " + String.format(Locale.US, "%.2f €", ivaTotal),
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            ivaSummary.setAlignment(Element.ALIGN_RIGHT);
            document.add(ivaSummary);
            document.add(Chunk.NEWLINE);
        }

        // ======== TOTAL GENERAL ========
        Paragraph total = new Paragraph("Importe total (IVA incluido): "
                + String.format(Locale.US, "%.2f €", totalConIVA),
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
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));
        document.add(new Paragraph("Gracias por su confianza.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

        document.close();
        System.out.println("PDF guardado en: " + desktopPath + fileName);
    }
}

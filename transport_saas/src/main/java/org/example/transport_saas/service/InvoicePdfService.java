package org.example.transport_saas.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.example.transport_saas.entity.Invoice;
import org.example.transport_saas.entity.InvoiceItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generatePdf(Invoice invoice) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

            Paragraph title = new Paragraph("ФАКТУРА № " + invoice.getInvoiceNumber(), titleFont);
            title.setSpacingAfter(20);
            document.add(title);

            // ---- От / До ----
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            PdfPCell fromCell = new PdfPCell();
            fromCell.setBorder(Rectangle.NO_BORDER);
            fromCell.addElement(new Paragraph("ДОСТАВЧИК", labelFont));
            fromCell.addElement(new Paragraph(
                    invoice.getCompany() != null ? invoice.getCompany().getName() : "", normalFont));
            if (invoice.getCompany() != null) {
                if (invoice.getCompany().getBulstat() != null) {
                    fromCell.addElement(new Paragraph("ЕИК: " + invoice.getCompany().getBulstat(), normalFont));
                }
                if (invoice.getCompany().isVatRegistered() && invoice.getCompany().getVatNumber() != null) {
                    fromCell.addElement(new Paragraph("ДДС №: " + invoice.getCompany().getVatNumber(), normalFont));
                }
                if (invoice.getCompany().getAddress() != null) {
                    fromCell.addElement(new Paragraph(invoice.getCompany().getAddress(), normalFont));
                }
                if (invoice.getCompany().getMol() != null) {
                    fromCell.addElement(new Paragraph("МОЛ: " + invoice.getCompany().getMol(), normalFont));
                }
                if (invoice.getCompany().getIban() != null) {
                    fromCell.addElement(new Paragraph("IBAN: " + invoice.getCompany().getIban(), normalFont));
                }
            }

            PdfPCell toCell = new PdfPCell();
            toCell.setBorder(Rectangle.NO_BORDER);
            toCell.addElement(new Paragraph("ПОЛУЧАТЕЛ", labelFont));
            toCell.addElement(new Paragraph(
                    invoice.getClient() != null ? invoice.getClient().getName() : "", normalFont));
            if (invoice.getClient() != null) {
                if (invoice.getClient().getBulstat() != null) {
                    toCell.addElement(new Paragraph("ЕИК: " + invoice.getClient().getBulstat(), normalFont));
                }
                if (invoice.getClient().getAddress() != null) {
                    toCell.addElement(new Paragraph(invoice.getClient().getAddress(), normalFont));
                }
            }

            infoTable.addCell(fromCell);
            infoTable.addCell(toCell);
            document.add(infoTable);

            // ---- Дати ----
            Paragraph dates = new Paragraph();
            dates.add(new Chunk("Дата на издаване: ", labelFont));
            dates.add(new Chunk(
                    invoice.getIssueDate() != null ? invoice.getIssueDate().format(DATE_FMT) : "-", normalFont));
            dates.add(Chunk.NEWLINE);
            dates.add(new Chunk("Падеж: ", labelFont));
            dates.add(new Chunk(
                    invoice.getDueDate() != null ? invoice.getDueDate().format(DATE_FMT) : "-", normalFont));
            dates.setSpacingAfter(20);
            document.add(dates);

            // ---- Таблица с редовете ----
            PdfPTable table = new PdfPTable(new float[]{5f, 2f});
            table.setWidthPercentage(100);
            table.setSpacingAfter(15);

            PdfPCell h1 = new PdfPCell(new Phrase("Описание", headerFont));
            h1.setBackgroundColor(new Color(30, 30, 30));
            h1.setPadding(6);
            PdfPCell h2 = new PdfPCell(new Phrase("Сума (€)", headerFont));
            h2.setBackgroundColor(new Color(30, 30, 30));
            h2.setPadding(6);
            table.addCell(h1);
            table.addCell(h2);

            for (InvoiceItem item : invoice.getItems()) {
                PdfPCell c1 = new PdfPCell(new Phrase(item.getDescription(), normalFont));
                c1.setPadding(6);
                PdfPCell c2 = new PdfPCell(new Phrase(
                        item.getAmount() != null ? item.getAmount().toPlainString() : "0", normalFont));
                c2.setPadding(6);
                table.addCell(c1);
                table.addCell(c2);
            }

            document.add(table);

            PdfPTable totalsTable = new PdfPTable(new float[]{5f, 2f});
            totalsTable.setWidthPercentage(100);
            totalsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            addTotalsRow(totalsTable, "Данъчна основа:", invoice.getTotalAmount(), normalFont);

            if (invoice.getVatRate() != null && invoice.getVatRate().signum() > 0) {
                addTotalsRow(totalsTable,
                        "ДДС (" + invoice.getVatRate().stripTrailingZeros().toPlainString() + "%):",
                        invoice.getVatAmount(), normalFont);
                addTotalsRow(totalsTable, "ОБЩО ЗА ПЛАЩАНЕ:", invoice.getGrandTotal(),
                        new Font(Font.HELVETICA, 13, Font.BOLD));
            } else {
                addTotalsRow(totalsTable, "ОБЩО ЗА ПЛАЩАНЕ:", invoice.getTotalAmount(),
                        new Font(Font.HELVETICA, 13, Font.BOLD));
            }

            document.add(totalsTable);

            if (invoice.getVatRate() == null || invoice.getVatRate().signum() == 0) {
                Paragraph vatNote = new Paragraph(
                        "Основание за неначисляване на ДДС: доставчикът не е регистриран по ЗДДС.",
                        new Font(Font.HELVETICA, 8, Font.ITALIC));
                vatNote.setSpacingBefore(6);
                document.add(vatNote);
            }

            if (invoice.getNotes() != null && !invoice.getNotes().isBlank()) {
                Paragraph notes = new Paragraph();
                notes.setSpacingBefore(20);
                notes.add(new Chunk("Бележки: ", labelFont));
                notes.add(new Chunk(invoice.getNotes(), normalFont));
                document.add(notes);
            }

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Грешка при генериране на фактурата", e);
        }
    }

    private void addTotalsRow(PdfPTable table, String label, java.math.BigDecimal amount, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(4);

        PdfPCell valueCell = new PdfPCell(new Phrase(
                (amount != null ? amount.toPlainString() : "0") + " €", font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(4);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}

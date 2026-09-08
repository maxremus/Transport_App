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
            if (invoice.getCompany() != null && invoice.getCompany().getBulstat() != null) {
                fromCell.addElement(new Paragraph("ЕИК: " + invoice.getCompany().getBulstat(), normalFont));
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

            Paragraph total = new Paragraph(
                    "ОБЩО: " + (invoice.getTotalAmount() != null ? invoice.getTotalAmount().toPlainString() : "0") + " €",
                    new Font(Font.HELVETICA, 13, Font.BOLD));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

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
}

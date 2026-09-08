package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.transport_saas.entity.Trip;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Генерира Excel (.xlsx) справка за пътуванията на фирмата - за
 * счетоводителя, без да се налага ръчно копиране от екрана.
 */
@Service
@RequiredArgsConstructor
public class TripExportService {

    private static final String[] HEADERS = {
            "Дата", "От", "До", "МПС", "Клиент", "Приход",
            "Гориво", "Пътни такси", "Други разходи", "Общо разходи", "Печалба"
    };

    public byte[] exportTrips(List<Trip> trips) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Курсове");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd.mm.yyyy"));

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalFuel = BigDecimal.ZERO;
            BigDecimal totalToll = BigDecimal.ZERO;
            BigDecimal totalOther = BigDecimal.ZERO;
            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal totalProfit = BigDecimal.ZERO;

            for (Trip t : trips) {
                Row row = sheet.createRow(rowIdx++);

                if (t.getTripDate() != null) {
                    Cell dateCell = row.createCell(0);
                    dateCell.setCellValue(java.sql.Date.valueOf(t.getTripDate()));
                    dateCell.setCellStyle(dateStyle);
                } else {
                    row.createCell(0).setCellValue("");
                }

                row.createCell(1).setCellValue(nullToEmpty(t.getFromLocation()));
                row.createCell(2).setCellValue(nullToEmpty(t.getToLocation()));
                row.createCell(3).setCellValue(
                        t.getVehicle() != null ? nullToEmpty(t.getVehicle().getRegistrationNumber()) : "");
                row.createCell(4).setCellValue(
                        t.getClient() != null ? nullToEmpty(t.getClient().getName()) : "");

                BigDecimal revenue = orZero(t.getRevenue());
                BigDecimal fuel = orZero(t.getFuelCost());
                BigDecimal toll = orZero(t.getTollCost());
                BigDecimal other = orZero(t.getOtherCost());
                BigDecimal cost = t.getTotalCost() != null ? t.getTotalCost() : BigDecimal.ZERO;
                BigDecimal profit = t.getProfit() != null ? t.getProfit() : BigDecimal.ZERO;

                row.createCell(5).setCellValue(revenue.doubleValue());
                row.createCell(6).setCellValue(fuel.doubleValue());
                row.createCell(7).setCellValue(toll.doubleValue());
                row.createCell(8).setCellValue(other.doubleValue());
                row.createCell(9).setCellValue(cost.doubleValue());
                row.createCell(10).setCellValue(profit.doubleValue());

                totalRevenue = totalRevenue.add(revenue);
                totalFuel = totalFuel.add(fuel);
                totalToll = totalToll.add(toll);
                totalOther = totalOther.add(other);
                totalCost = totalCost.add(cost);
                totalProfit = totalProfit.add(profit);
            }

            // ред с общи суми накрая
            Row totalsRow = sheet.createRow(rowIdx + 1);
            Cell totalsLabel = totalsRow.createCell(3);
            totalsLabel.setCellValue("ОБЩО:");
            totalsLabel.setCellStyle(headerStyle);

            totalsRow.createCell(5).setCellValue(totalRevenue.doubleValue());
            totalsRow.createCell(6).setCellValue(totalFuel.doubleValue());
            totalsRow.createCell(7).setCellValue(totalToll.doubleValue());
            totalsRow.createCell(8).setCellValue(totalOther.doubleValue());
            totalsRow.createCell(9).setCellValue(totalCost.doubleValue());
            totalsRow.createCell(10).setCellValue(totalProfit.doubleValue());
            for (int i = 5; i <= 10; i++) {
                totalsRow.getCell(i).setCellStyle(headerStyle);
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Грешка при генериране на Excel файла", e);
        }
    }

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }

    private BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.DTO.FuelReportRow;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.repository.TripRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/fuel-report")
public class FuelReportController {

    private final TripRepository tripRepository;

    @GetMapping
    public String report(@RequestParam(required = false) Integer month,
                          @RequestParam(required = false) Integer year,
                          Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        // по подразбиране - текущия месец, за да не се смесват всички
        // курсове от началото на времето в една справка
        if (month == null && year == null) {
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        List<Object[]> raw = tripRepository.fuelReportByVehicle(companyId, month, year);

        List<FuelReportRow> rows = new ArrayList<>();
        BigDecimal grandFuel = BigDecimal.ZERO;
        long grandKm = 0;
        BigDecimal grandRevenue = BigDecimal.ZERO;
        BigDecimal grandProfit = BigDecimal.ZERO;

        for (Object[] r : raw) {
            String reg = (String) r[0];
            BigDecimal fuel = (BigDecimal) r[1];
            long km = ((Number) r[2]).longValue();
            BigDecimal revenue = (BigDecimal) r[3];
            BigDecimal profit = (BigDecimal) r[4];
            long trips = ((Number) r[5]).longValue();

            rows.add(new FuelReportRow(reg, fuel, km, revenue, profit, trips));

            grandFuel = grandFuel.add(fuel);
            grandKm += km;
            grandRevenue = grandRevenue.add(revenue);
            grandProfit = grandProfit.add(profit);
        }

        model.addAttribute("rows", rows);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("grandFuel", grandFuel);
        model.addAttribute("grandKm", grandKm);
        model.addAttribute("grandRevenue", grandRevenue);
        model.addAttribute("grandProfit", grandProfit);
        model.addAttribute("grandFuelPerKm",
                grandKm > 0 ? grandFuel.divide(BigDecimal.valueOf(grandKm), 3, java.math.RoundingMode.HALF_UP) : null);

        return "fuel-report";
    }
}

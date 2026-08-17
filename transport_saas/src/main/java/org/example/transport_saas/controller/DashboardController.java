package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.repository.TripRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.example.transport_saas.service.DriverIntegrationService;
import org.example.transport_saas.service.VehicleDocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDocumentService vehicleDocumentService;
    private final DriverIntegrationService driverIntegrationService;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            Model model
    ) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        System.out.println("COMPANY ID = " + companyId);

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        int selectedMonth = (month != null) ? month : currentMonth;
        int selectedYear = (year != null) ? year : currentYear;

        // ================= ФИНАНСИ =================
        BigDecimal revenue = tripRepository.monthlyRevenue(companyId, selectedMonth, selectedYear);
        BigDecimal profit = tripRepository.monthlyProfit(companyId, selectedMonth, selectedYear);
        BigDecimal expenses = tripRepository.monthlyExpenses(companyId, selectedMonth, selectedYear);

        // FIX: null -> 0
        revenue = (revenue != null) ? revenue : BigDecimal.ZERO;
        profit = (profit != null) ? profit : BigDecimal.ZERO;
        expenses = (expenses != null) ? expenses : BigDecimal.ZERO;

        // ================= ПРЕДИШЕН МЕСЕЦ =================
        LocalDate previous = LocalDate.of(selectedYear, selectedMonth, 1).minusMonths(1);

        BigDecimal previousRevenue = tripRepository.monthlyRevenue(
                companyId,
                previous.getMonthValue(),
                previous.getYear()
        );

        // FIX: null -> 0
        previousRevenue = (previousRevenue != null) ? previousRevenue : BigDecimal.ZERO;

        // ================= РЪСТ =================
        BigDecimal revenueGrowth = previousRevenue.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : revenue.subtract(previousRevenue)
                .divide(previousRevenue, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // ================= ГРАФИКА =================
        List<Object[]> chartData =
                tripRepository.revenueAndExpensePerMonth(companyId, selectedYear);

        // FIX: null -> празен списък
        chartData = (chartData != null) ? chartData : List.of();

        // ================= ТОП МПС =================
        List<Object[]> topVehicle =
                tripRepository.mostProfitableVehicle(companyId);

        Object topVehicleData =
                (topVehicle != null && !topVehicle.isEmpty()) ? topVehicle.get(0) : null;

        List<Object[]> topTrip =
                tripRepository.mostProfitableTrip(companyId, selectedMonth, selectedYear);

        Object topTripData =
                (topTrip != null && !topTrip.isEmpty()) ? topTrip.get(0) : null;

        model.addAttribute("topTrip", topTripData);

        // ================= MODEL =================
        model.addAttribute("revenue", revenue);
        model.addAttribute("profit", profit);
        model.addAttribute("expenses", expenses);
        model.addAttribute("revenueGrowth", revenueGrowth);
        model.addAttribute("chartData", chartData);
        model.addAttribute("topVehicle", topVehicleData);

        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("currentPage", "dashboard");

        model.addAttribute(
                "expiringDocs",
                vehicleDocumentService.getExpiringDocuments(companyId)
        );

        // driver-service е отделна услуга - ако временно е недостъпна, не
        // трябва да чупи цялото табло, затова хващаме грешката тук
        List<org.example.transport_saas.DTO.DriverDocumentRequestDTO> expiringDriverDocs;
        try {
            expiringDriverDocs = driverIntegrationService.getExpiringDocumentsForCompany(companyId);
        } catch (Exception e) {
            expiringDriverDocs = List.of();
        }
        model.addAttribute("expiringDriverDocs", expiringDriverDocs);

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        // Пренасочване към началната страница
        return "redirect:/";
    }
}

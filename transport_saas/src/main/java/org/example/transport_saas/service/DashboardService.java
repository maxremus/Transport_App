package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.DTO.DashboardStats;
import org.example.transport_saas.repository.ExpenseRepository;
import org.example.transport_saas.repository.TripRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    public DashboardStats getStats(Long companyId) {

        BigDecimal totalRevenue = tripRepository.totalRevenue(companyId);
        BigDecimal totalProfit = tripRepository.totalProfit(companyId);

        long totalTrips = tripRepository.countByCompanyId(companyId);
        long totalVehicles = vehicleRepository.countByCompanyId(companyId);

        BigDecimal totalExpenses = totalRevenue.subtract(totalProfit);

        BigDecimal margin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            margin = totalProfit
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new DashboardStats(
                totalRevenue.setScale(2, RoundingMode.HALF_UP),
                totalExpenses.setScale(2, RoundingMode.HALF_UP),
                totalProfit.setScale(2, RoundingMode.HALF_UP),
                margin.setScale(2, RoundingMode.HALF_UP),
                totalTrips,
                totalVehicles
        );
    }

    // 🔥 Месечна статистика
    public DashboardStats getMonthlyStats(Long companyId) {

        LocalDate now = LocalDate.now();

        BigDecimal monthlyRevenue =
                tripRepository.monthlyRevenue(companyId,
                        now.getMonthValue(),
                        now.getYear());

        BigDecimal monthlyProfit =
                tripRepository.monthlyProfit(companyId,
                        now.getMonthValue(),
                        now.getYear());

        BigDecimal monthlyExpenses =
                monthlyRevenue.subtract(monthlyProfit);

        BigDecimal margin = BigDecimal.ZERO;
        if (monthlyRevenue.compareTo(BigDecimal.ZERO) > 0) {
            margin = monthlyProfit
                    .divide(monthlyRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new DashboardStats(
                monthlyRevenue.setScale(2, RoundingMode.HALF_UP),
                monthlyExpenses.setScale(2, RoundingMode.HALF_UP),
                monthlyProfit.setScale(2, RoundingMode.HALF_UP),
                margin.setScale(2, RoundingMode.HALF_UP),
                0,
                0
        );
    }
}

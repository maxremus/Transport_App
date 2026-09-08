package org.example.transport_saas.DTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Един ред от справката "Гориво по МПС" - агрегирани данни за конкретно
 * МПС за избрания период.
 */
public class FuelReportRow {

    private final String registrationNumber;
    private final BigDecimal totalFuelCost;
    private final long totalDistanceKm;
    private final BigDecimal totalRevenue;
    private final BigDecimal totalProfit;
    private final long tripCount;

    public FuelReportRow(String registrationNumber, BigDecimal totalFuelCost, long totalDistanceKm,
                          BigDecimal totalRevenue, BigDecimal totalProfit, long tripCount) {
        this.registrationNumber = registrationNumber;
        this.totalFuelCost = totalFuelCost;
        this.totalDistanceKm = totalDistanceKm;
        this.totalRevenue = totalRevenue;
        this.totalProfit = totalProfit;
        this.tripCount = tripCount;
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public BigDecimal getTotalFuelCost() { return totalFuelCost; }
    public long getTotalDistanceKm() { return totalDistanceKm; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTotalProfit() { return totalProfit; }
    public long getTripCount() { return tripCount; }

    /** €/км гориво - null ако няма въведени километри за периода. */
    public BigDecimal getFuelCostPerKm() {
        if (totalDistanceKm == 0 || totalFuelCost == null) return null;
        return totalFuelCost.divide(BigDecimal.valueOf(totalDistanceKm), 3, RoundingMode.HALF_UP);
    }

    /** Печалба на километър - null ако няма въведени километри за периода. */
    public BigDecimal getProfitPerKm() {
        if (totalDistanceKm == 0 || totalProfit == null) return null;
        return totalProfit.divide(BigDecimal.valueOf(totalDistanceKm), 3, RoundingMode.HALF_UP);
    }
}

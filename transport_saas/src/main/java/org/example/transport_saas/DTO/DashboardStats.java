package org.example.transport_saas.DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardStats {

    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal totalProfit;
    private BigDecimal profitMargin;
    private long totalTrips;
    private long totalVehicles;
}

package org.example.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromLocation;
    private String toLocation;

    @Column(nullable = false)
    private BigDecimal revenue;

    @Column(nullable = false)
    private BigDecimal fuelCost = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal tollCost = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal otherCost = BigDecimal.ZERO;

    private LocalDate tripDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public BigDecimal getTotalCost() {
        BigDecimal fuel = fuelCost != null ? fuelCost : BigDecimal.ZERO;
        BigDecimal toll = tollCost != null ? tollCost : BigDecimal.ZERO;
        BigDecimal other = otherCost != null ? otherCost : BigDecimal.ZERO;
        return fuel.add(toll).add(other);
    }

    public BigDecimal getProfit() {
        BigDecimal rev = revenue != null ? revenue : BigDecimal.ZERO;
        return rev.subtract(getTotalCost());
    }
}

package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByCompanyId(Long companyId);

    List<Trip> findByCompanyIdAndTripDateBetween(
            Long companyId,
            LocalDate start,
            LocalDate end
    );

    long countByCompanyId(Long companyId);

    @Query("""
        SELECT COALESCE(SUM(
            t.revenue -
            COALESCE(t.fuelCost,0) -
            COALESCE(t.tollCost,0) -
            COALESCE(t.otherCost,0)
        ),0)
        FROM Trip t
        WHERE t.company.id = :companyId
    """)
    BigDecimal totalProfit(Long companyId);

    @Query("""
        SELECT COALESCE(SUM(t.revenue),0)
        FROM Trip t
        WHERE t.company.id = :companyId
    """)
    BigDecimal totalRevenue(Long companyId);

    @Query("""
    SELECT COALESCE(SUM(t.revenue),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND MONTH(t.tripDate) = :month
      AND YEAR(t.tripDate) = :year
""")
    BigDecimal monthlyRevenue(Long companyId, int month, int year);

    @Query("""
    SELECT COALESCE(SUM(
        t.revenue -
        COALESCE(t.fuelCost,0) -
        COALESCE(t.tollCost,0) -
        COALESCE(t.otherCost,0)
    ),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND MONTH(t.tripDate) = :month
      AND YEAR(t.tripDate) = :year
""")
    BigDecimal monthlyProfit(Long companyId, int month, int year);

    @Query("""
    SELECT COALESCE(SUM(t.revenue),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND (:month IS NULL OR MONTH(t.tripDate) = :month)
      AND (:year IS NULL OR YEAR(t.tripDate) = :year)
""")
    BigDecimal revenueFiltered(Long companyId, Integer month, Integer year);

    @Query("""
    SELECT COALESCE(SUM(
        t.revenue -
        COALESCE(t.fuelCost,0) -
        COALESCE(t.tollCost,0) -
        COALESCE(t.otherCost,0)
    ),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND (:month IS NULL OR MONTH(t.tripDate) = :month)
      AND (:year IS NULL OR YEAR(t.tripDate) = :year)
""")
    BigDecimal profitFiltered(Long companyId, Integer month, Integer year);

    @Query("""
    SELECT COALESCE(SUM(t.revenue),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND YEAR(t.tripDate) = :year
""")
    BigDecimal yearlyRevenue(Long companyId, Integer year);


    @Query("""
    SELECT COALESCE(SUM(
        t.revenue -
        COALESCE(t.fuelCost,0) -
        COALESCE(t.tollCost,0) -
        COALESCE(t.otherCost,0)
    ),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND YEAR(t.tripDate) = :year
""")
    BigDecimal yearlyProfit(Long companyId, Integer year);

    @Query("""
    SELECT MONTH(t.tripDate),
           COALESCE(SUM(t.revenue),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND YEAR(t.tripDate) = :year
    GROUP BY MONTH(t.tripDate)
    ORDER BY MONTH(t.tripDate)
""")
    List<Object[]> revenuePerMonth(Long companyId, int year);

    @Query("""
    SELECT MONTH(t.tripDate),
           COALESCE(SUM(
               t.revenue -
               COALESCE(t.fuelCost,0) -
               COALESCE(t.tollCost,0) -
               COALESCE(t.otherCost,0)
           ),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND YEAR(t.tripDate) = :year
    GROUP BY MONTH(t.tripDate)
    ORDER BY MONTH(t.tripDate)
""")
    List<Object[]> profitPerMonth(Long companyId, int year);

    @Query("""
    SELECT COALESCE(SUM(
        COALESCE(t.fuelCost,0) +
        COALESCE(t.tollCost,0) +
        COALESCE(t.otherCost,0)
    ),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND MONTH(t.tripDate) = :month
      AND YEAR(t.tripDate) = :year
""")
    BigDecimal monthlyExpenses(Long companyId, int month, int year);

    @Query("""
    SELECT MONTH(t.tripDate),
           COALESCE(SUM(t.revenue),0),
           COALESCE(SUM(
               COALESCE(t.fuelCost,0) +
               COALESCE(t.tollCost,0) +
               COALESCE(t.otherCost,0)
           ),0)
    FROM Trip t
    WHERE t.company.id = :companyId
      AND YEAR(t.tripDate) = :year
    GROUP BY MONTH(t.tripDate)
    ORDER BY MONTH(t.tripDate)
""")
    List<Object[]> revenueAndExpensePerMonth(Long companyId, int year);

    @Query("""
    SELECT v.registrationNumber,
           COALESCE(SUM(
               t.revenue -
               COALESCE(t.fuelCost,0) -
               COALESCE(t.tollCost,0) -
               COALESCE(t.otherCost,0)
           ),0)
    FROM Trip t
    JOIN t.vehicle v
    WHERE t.company.id = :companyId
    GROUP BY v.registrationNumber
    ORDER BY 2 DESC
""")
    List<Object[]> mostProfitableVehicle(Long companyId);


    @Query("""
    SELECT 
        t.id,
        t.tripDate,
        (t.revenue -
         COALESCE(t.fuelCost,0) -
         COALESCE(t.tollCost,0) -
         COALESCE(t.otherCost,0)
        ) as profit
    FROM Trip t
    WHERE t.company.id = :companyId
      AND MONTH(t.tripDate) = :month
      AND YEAR(t.tripDate) = :year
    ORDER BY profit DESC
""")
    List<Object[]> mostProfitableTrip(Long companyId, int month, int year);
}



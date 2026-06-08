package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCompanyId(Long companyId);

    long countByCompanyId(Long companyId);

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
""")
    List<Object[]> profitPerVehicle(Long companyId);


    Optional<Vehicle> findById(Long vehicleId);

    Optional<Vehicle> findByIdAndCompanyId(Long vehicleId, Long companyId);
}

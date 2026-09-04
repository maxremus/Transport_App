package org.example.adminservice.repository;

import org.example.adminservice.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCompanyId(Long companyId);

    // JOIN FETCH зарежда company в същата заявка, за да не гърми
    // LazyInitializationException в темплейта (open-in-view=false)
    @Query("select v from Vehicle v left join fetch v.company")
    List<Vehicle> findAllWithCompany();
}

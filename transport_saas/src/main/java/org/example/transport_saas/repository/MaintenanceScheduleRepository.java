package org.example.transport_saas.repository;

import org.example.transport_saas.entity.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, Long> {

    @Query("select m from MaintenanceSchedule m join fetch m.vehicle v where v.company.id = :companyId order by v.registrationNumber")
    List<MaintenanceSchedule> findAllForCompany(Long companyId);

    List<MaintenanceSchedule> findByVehicleId(Long vehicleId);
}

package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findById(Long driverId);

    List<Driver> findByCompanyId(Long companyId);

    long countByCompanyId(Long companyId);

    Optional<Driver> findByIdAndCompanyId(Long driverId, Long companyId);
}

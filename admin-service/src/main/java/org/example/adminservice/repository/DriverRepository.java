package org.example.adminservice.repository;

import org.example.adminservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByCompanyId(Long companyId);
}

package org.example.adminservice.repository;

import org.example.adminservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCompanyId(Long companyId);
}

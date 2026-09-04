package org.example.adminservice.repository;

import org.example.adminservice.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByCompanyId(Long companyId);

    // JOIN FETCH зарежда company в същата заявка, за да не гърми
    // LazyInitializationException в темплейта (open-in-view=false)
    @Query("select t from Trip t left join fetch t.company")
    List<Trip> findAllWithCompany();
}

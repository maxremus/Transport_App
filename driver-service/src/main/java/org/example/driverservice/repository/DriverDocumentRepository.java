package org.example.driverservice.repository;

import org.example.driverservice.entity.DriverDocument;
import org.example.driverservice.entity.DriverDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {

    Optional<DriverDocument> findByDriverIdAndType(
            Long driverId,
            DriverDocumentType type
    );

    List<DriverDocument> findByDriverId(Long driverId);
}

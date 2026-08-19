package org.example.adminservice.repository;

import org.example.adminservice.entity.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {
    List<DriverDocument> findByDriverId(Long driverId);
}

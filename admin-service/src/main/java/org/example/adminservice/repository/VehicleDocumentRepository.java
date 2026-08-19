package org.example.adminservice.repository;

import org.example.adminservice.entity.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {
}

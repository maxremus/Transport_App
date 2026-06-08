package org.example.transport_saas.repository;

import org.example.transport_saas.entity.DocumentType;
import org.example.transport_saas.entity.VehicleDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocument, Long> {

    @Query("""
    SELECT d FROM VehicleDocument d
    WHERE d.vehicle.company.id = :companyId
    AND d.expiryDate <= :alertDate
    """)
    List<VehicleDocument> findExpiringDocuments(
            Long companyId,
            LocalDate alertDate
    );

    Optional<VehicleDocument>findByVehicleIdAndType(Long vehicleId, DocumentType type);

    List<VehicleDocument> findByVehicleCompanyId(Long companyId);
}

package org.example.transport_saas.repository;

import org.example.transport_saas.entity.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {

    List<DriverDocument> findByDriverId(Long driverId);

    // за проверка, че документът принадлежи на текущата фирма преди редакция
    Optional<DriverDocument> findByIdAndDriverCompanyId(Long id, Long companyId);
}

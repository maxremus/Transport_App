package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCompanyIdOrderByIssueDateDesc(Long companyId);

    long countByCompanyId(Long companyId);
}

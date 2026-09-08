package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByCompanyIdOrderByNameAsc(Long companyId);
}

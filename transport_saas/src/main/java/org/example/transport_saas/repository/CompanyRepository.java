package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByStripeSubscriptionId(String subscriptionId);

}

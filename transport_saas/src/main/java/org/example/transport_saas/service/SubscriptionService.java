package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.Subscription;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.exception.SubscriptionException;
import org.example.transport_saas.repository.CompanyRepository;
import org.example.transport_saas.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final CompanyRepository companyRepository;
    private final SubscriptionRepository subscriptionRepository;

    public void validateSubscription(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new SubscriptionException("Company not found"));

        if (!company.isActive()) {
            throw new SubscriptionException("Subscription inactive");
        }

        if (company.getSubscriptionExpiry() != null &&
                company.getSubscriptionExpiry().isBefore(LocalDate.now())) {
            throw new SubscriptionException("Subscription expired");
        }

        if (company.getTrialEndsAt() != null &&
                company.getTrialEndsAt().isBefore(LocalDate.now())) {
            throw new SubscriptionException("Trial expired");
        }
    }


    public Optional<Subscription> getActive(User user) {
        return subscriptionRepository.findByUser(user)
                .stream()
                .filter(sub ->
                        sub.isActive() &&
                                sub.getEndDate().isAfter(ChronoLocalDate.from(LocalDateTime.now())))
                .findFirst();
    }
}

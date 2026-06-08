package org.example.transport_saas.controller;

import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.SubscriptionPlan;
import org.example.transport_saas.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyApiController {

    private final CompanyRepository companyRepository;

    public CompanyApiController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Value("${app.internal-key}")
    private String internalKey;


    private void validateKey(String apiKey) {
        if (!internalKey.equals(apiKey)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    @PostMapping("/update-plan-by-subscription")
    public void updatePlanBySubscription(
            @RequestParam String subscriptionId,
            @RequestParam String plan,
            @RequestHeader("X-API-KEY") String apiKey) {

        validateKey(apiKey);

        Company company = companyRepository
                .findByStripeSubscriptionId(subscriptionId)
                .orElseThrow();

        company.setSubscriptionPlan(SubscriptionPlan.valueOf(plan));
        company.setActive(true);

        LocalDate baseDate = company.getSubscriptionExpiry() != null &&
                company.getSubscriptionExpiry().isAfter(LocalDate.now())
                ? company.getSubscriptionExpiry()
                : LocalDate.now();

        company.setSubscriptionExpiry(baseDate.plusMonths(1));

        companyRepository.save(company);

        System.out.println("UPDATED PLAN VIA SUB: " + plan);
    }

    @PostMapping("/update-plan")
    public void updatePlan(
            @RequestParam Long companyId,
            @RequestParam String plan,
            @RequestParam String subscriptionId,
            @RequestHeader("X-API-KEY") String apiKey) {

        validateKey(apiKey);

        Company company = companyRepository.findById(companyId)
                .orElseThrow();

        SubscriptionPlan selectedPlan =
                SubscriptionPlan.valueOf(plan.toUpperCase());

        company.setSubscriptionPlan(selectedPlan);
        company.setActive(true);
        company.setStripeSubscriptionId(subscriptionId);

        LocalDate baseDate = company.getSubscriptionExpiry() != null &&
                company.getSubscriptionExpiry().isAfter(LocalDate.now())
                ? company.getSubscriptionExpiry()
                : LocalDate.now();

        company.setSubscriptionExpiry(baseDate.plusMonths(1));

        companyRepository.save(company);

        System.out.println("UPDATED PLAN: " + plan);
    }

    @PostMapping("/renew")
    public void renew(
            @RequestParam String subscriptionId,
            @RequestHeader("X-API-KEY") String apiKey) {

        validateKey(apiKey);

        Company company = companyRepository
                .findByStripeSubscriptionId(subscriptionId)
                .orElseThrow();

        LocalDate baseDate = company.getSubscriptionExpiry() != null &&
                company.getSubscriptionExpiry().isAfter(LocalDate.now())
                ? company.getSubscriptionExpiry()
                : LocalDate.now();

        company.setSubscriptionExpiry(baseDate.plusMonths(1));

        companyRepository.save(company);

        System.out.println("RENEWED: " + subscriptionId);
    }

    @PostMapping("/deactivate")
    public void deactivate(
            @RequestParam String subscriptionId,
            @RequestHeader("X-API-KEY") String apiKey) {

        validateKey(apiKey);

        Company company = companyRepository
                .findByStripeSubscriptionId(subscriptionId)
                .orElseThrow();

        company.setActive(false);

        companyRepository.save(company);

        System.out.println("DEACTIVATED: " + subscriptionId);
    }
}

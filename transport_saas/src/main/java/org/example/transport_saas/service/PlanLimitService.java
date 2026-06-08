package org.example.transport_saas.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.exception.PlanLimitExceededException;
import org.example.transport_saas.repository.CompanyRepository;
import org.example.transport_saas.repository.TripRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class PlanLimitService {

    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;

    public void checkVehicleLimit(Long companyId) {

        int limit = getVehicleLimit(companyId);
        long count = vehicleRepository.countByCompanyId(companyId);

        if (count >= limit) {
            throw new PlanLimitExceededException("Достигнат е лимитът за МПС.");
        }
    }

    public void checkTripLimit(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow();

        long tripCount = tripRepository.countByCompanyId(companyId);

        int maxTrips = switch (company.getSubscriptionPlan()) {
            case BASIC -> 100;
            case PRO -> 1000;
            case PREMIUM -> Integer.MAX_VALUE;
        };

        if (tripCount >= maxTrips) {
            throw new RuntimeException("Trip limit reached. Upgrade your plan.");
        }
    }

    public int getVehicleLimit(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow();

        return switch (company.getSubscriptionPlan()) {
            case BASIC -> 3;
            case PRO -> 10;
            case PREMIUM -> Integer.MAX_VALUE;
        };
    }

    public int getTripLimit(Long companyId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow();

        return switch (company.getSubscriptionPlan()) {
            case BASIC -> 100;
            case PRO -> 1000;
            case PREMIUM -> Integer.MAX_VALUE;
        };
    }
}

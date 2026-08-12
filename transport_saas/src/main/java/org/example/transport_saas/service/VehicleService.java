package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final PlanLimitService planLimitService;

    public List<Vehicle> getAllForCompany(Long companyId) {
        return vehicleRepository.findByCompanyId(companyId);
    }
    public void save(Vehicle vehicle, Long companyId) {

        planLimitService.checkVehicleLimit(companyId);

        vehicle.setCompany(
                Company.builder().id(companyId).build()
        );

        vehicleRepository.save(vehicle);
    }

    public void update(Long vehicleId, Long companyId, Vehicle updated) {

        Vehicle vehicle = getVehicleIfBelongsToCompany(vehicleId, companyId);
        if (vehicle == null) {
            throw new RuntimeException("Access denied");
        }

        vehicle.setRegistrationNumber(updated.getRegistrationNumber());

        if (updated.getVin() != null) {
            vehicle.setVin(updated.getVin());
        }

        vehicleRepository.save(vehicle);
    }

    public String getStatus(LocalDate expiryDate) {
        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) {
            return "EXPIRED";
        } else if (expiryDate.isBefore(now.plusDays(10))) {
            return "URGENT";
        } else if (expiryDate.isBefore(now.plusDays(30))) {
            return "WARNING";
        }

        return "OK";
    }

    public Vehicle getById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId).orElse(null);
    }

    public Vehicle getVehicleIfBelongsToCompany(Long vehicleId, Long companyId) {
        return vehicleRepository.findByIdAndCompanyId(vehicleId, companyId).orElse(null);
    }
}

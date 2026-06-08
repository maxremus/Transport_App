package org.example.transport_saas.service;

import org.example.transport_saas.Api.DriverClient;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.Driver;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.repository.DriverRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverClient driverClient;

    public DriverService(DriverRepository driverRepository, VehicleRepository vehicleRepository, DriverClient driverClient) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverClient = driverClient;
    }

    public void assignDriver(Long driverId, Long vehicleId) {
        // Логика за назначаване на шофьор към МПС
        Long companyId = SecurityUtils.getCurrentCompanyId();

        Driver driver = driverRepository.findByIdAndCompanyId(driverId, companyId).orElseThrow();
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();

        driver.setVehicle(vehicle);
        driver.setAvailable(false);

        driverRepository.save(driver);
    }

    public void createDriver(String name, String phone) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        Driver driver = Driver.builder()
                .name(name)
                .phone(phone)
                .available(true)
                .company(new Company())
                .build();
        driver.getCompany().setId(companyId);

        driverRepository.save(driver);
    }

    public List<Driver> getDriversByCompany() {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        return driverRepository.findByCompanyId(companyId);
    }

    public Driver getDriverIfBelongsToCompany(Long driverId, Long companyId) {
        return driverRepository.findByIdAndCompanyId(driverId, companyId).orElse(null);
    }
}

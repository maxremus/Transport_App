package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.Api.DriverClient;
import org.example.transport_saas.DTO.DriverCreateDTO;
import org.example.transport_saas.DTO.DriverDTO;
import org.example.transport_saas.DTO.DriverDocumentRequestDTO;
import org.example.transport_saas.entity.Driver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverIntegrationService {

    private final DriverClient driverClient;
    private final DriverService driverService;

    public void createDoc(Long driverId, String type, String number, String expiryDate) {

        DriverDocumentRequestDTO dto = new DriverDocumentRequestDTO();

        dto.setDriverId(driverId);
        dto.setType(type);
        dto.setExpiryDate(expiryDate);
        dto.setNumber(number);

        driverClient.save(dto);
    }

    public List<Driver> getAllDrivers() {
        return driverService.getDriversByCompany();
    }

    public void createDriver(String name, String phone) {
        driverService.createDriver(name, phone);

        DriverCreateDTO dto = new DriverCreateDTO();
        dto.setName(name);
        dto.setPhone(phone);
        driverClient.createDriver(dto);
    }

    public Driver getDriverIfBelongsToCompany(Long driverId, Long companyId) {
        return driverService.getDriverIfBelongsToCompany(driverId, companyId);
    }
}

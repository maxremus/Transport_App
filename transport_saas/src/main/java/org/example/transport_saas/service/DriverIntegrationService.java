package org.example.transport_saas.service;

import org.example.transport_saas.Api.DriverClient;
import org.example.transport_saas.DTO.DriverCreateDTO;
import org.example.transport_saas.DTO.DriverDTO;
import org.example.transport_saas.DTO.DriverDocumentRequestDTO;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.DriverDocumentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Шофьорите и документите им се управляват изцяло от driver-service.
 * transport_saas е само клиент - удостоверява се пред driver-service
 * с общ таен ключ (X-API-KEY, съвпада с app.internal-key в driver-service).
 */
@Service
public class DriverIntegrationService {

    private final DriverClient driverClient;

    @Value("${app.internal-key}")
    private String internalKey;

    public DriverIntegrationService(DriverClient driverClient) {
        this.driverClient = driverClient;
    }

    public List<DriverDTO> getAllDrivers() {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        return driverClient.getAllDrivers(companyId, internalKey);
    }

    public DriverDTO createDriver(String name, String phone) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        DriverCreateDTO dto = new DriverCreateDTO();
        dto.setName(name);
        dto.setPhone(phone);
        dto.setCompanyId(companyId);

        return driverClient.createDriver(dto, internalKey);
    }

    public DriverDTO getDriverIfBelongsToCompany(Long driverId, Long companyId) {
        try {
            return driverClient.getDriver(driverId, companyId, internalKey);
        } catch (Exception e) {
            return null;
        }
    }

    public List<DriverDocumentRequestDTO> getDocumentsForDriver(Long driverId) {
        return driverClient.getDocuments(driverId, internalKey);
    }

    public void createDocument(Long driverId, DriverDocumentType type, String number, LocalDate expiryDate) {
        DriverDocumentRequestDTO dto = new DriverDocumentRequestDTO();
        dto.setDriverId(driverId);
        dto.setType(type);
        dto.setNumber(number);
        dto.setExpiryDate(expiryDate);

        driverClient.createDocument(dto, internalKey);
    }

    public void updateDocument(Long id, DriverDocumentType type, String number, LocalDate expiryDate) {
        DriverDocumentRequestDTO dto = new DriverDocumentRequestDTO();
        dto.setType(type);
        dto.setNumber(number);
        dto.setExpiryDate(expiryDate);

        driverClient.updateDocument(id, dto, internalKey);
    }
}

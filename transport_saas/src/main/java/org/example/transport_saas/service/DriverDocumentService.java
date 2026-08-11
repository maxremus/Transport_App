package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Driver;
import org.example.transport_saas.entity.DriverDocument;
import org.example.transport_saas.entity.DriverDocumentType;
import org.example.transport_saas.repository.DriverDocumentRepository;
import org.example.transport_saas.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverDocumentService {

    private final DriverDocumentRepository driverDocumentRepository;
    private final DriverRepository driverRepository;

    public void create(Long driverId, DriverDocumentType type, String number, LocalDate expiryDate) {

        Driver driver = driverRepository.findById(driverId).orElseThrow();

        DriverDocument doc = DriverDocument.builder()
                .driver(driver)
                .type(type)
                .number(number)
                .expiryDate(expiryDate)
                .build();

        driverDocumentRepository.save(doc);
    }

    public void update(Long id, DriverDocumentType type, String number, LocalDate expiryDate) {

        DriverDocument doc = driverDocumentRepository.findById(id).orElseThrow();

        doc.setType(type);
        doc.setNumber(number);
        doc.setExpiryDate(expiryDate);

        driverDocumentRepository.save(doc);
    }

    public List<DriverDocument> getForDriver(Long driverId) {
        return driverDocumentRepository.findByDriverId(driverId);
    }

    public DriverDocument getIfBelongsToCompany(Long id, Long companyId) {
        return driverDocumentRepository.findByIdAndDriverCompanyId(id, companyId).orElse(null);
    }

    public String getDisplayType(DriverDocumentType type) {
        return switch (type) {
            case DRIVING_LICENSE -> "Книжка";
            case ID_CARD -> "Лична карта";
            case DIGITAL_CARD -> "Дигитална карта";
            case PROFESSIONAL_CERT -> "Професионална";
        };
    }

    public String getDisplayStatus(LocalDate expiryDate) {

        if (expiryDate == null) {
            return "—";
        }

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) {
            return "ИЗТЕКЪЛ";
        }

        if (expiryDate.isBefore(now.plusDays(10))) {
            return "СПЕШЕН";
        }

        if (expiryDate.isBefore(now.plusDays(30))) {
            return "ПРЕДУПРЕЖДЕНИЕ";
        }

        return "ОК";
    }
}

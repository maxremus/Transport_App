package org.example.driverservice.service;

import lombok.RequiredArgsConstructor;
import org.example.driverservice.entity.DriverDocument;
import org.example.driverservice.entity.DriverDocumentType;
import org.example.driverservice.repository.DriverDocumentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DriverDocumentService {

    private final DriverDocumentRepository driverDocumentRepository;

    public DriverDocumentService(DriverDocumentRepository driverDocumentRepository) {
        this.driverDocumentRepository = driverDocumentRepository;
    }

    public void saveOrUpdate(Long driverId,
                             DriverDocumentType type,
                             LocalDate expiryDate,
                             String number) {

        Optional<DriverDocument> existing =
                driverDocumentRepository.findByDriverIdAndType(driverId, type);

        if (existing.isPresent()) {
            DriverDocument doc = existing.get();
            doc.setExpiryDate(expiryDate);
            doc.setDocumentNumber(number);
            driverDocumentRepository.save(doc);
        } else {
            DriverDocument doc = new DriverDocument();
            doc.setDriverId(driverId);
            doc.setType(type);
            doc.setExpiryDate(expiryDate);
            doc.setDocumentNumber(number);
            driverDocumentRepository.save(doc);
        }
    }

    public String getStatus(LocalDate expiryDate) {

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) return "EXPIRED";
        if (expiryDate.isBefore(now.plusDays(10))) return "URGENT";
        if (expiryDate.isBefore(now.plusDays(30))) return "WARNING";

        return "OK";
    }
}

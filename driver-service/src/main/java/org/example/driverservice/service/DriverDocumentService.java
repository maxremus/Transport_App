package org.example.driverservice.service;

import org.example.driverservice.entity.Driver;
import org.example.driverservice.entity.DriverDocument;
import org.example.driverservice.entity.DriverDocumentType;
import org.example.driverservice.repository.DriverDocumentRepository;
import org.example.driverservice.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DriverDocumentService {

    private final DriverDocumentRepository driverDocumentRepository;
    private final DriverRepository driverRepository;

    public DriverDocumentService(DriverDocumentRepository driverDocumentRepository,
                                  DriverRepository driverRepository) {
        this.driverDocumentRepository = driverDocumentRepository;
        this.driverRepository = driverRepository;
    }

    public DriverDocument create(Long driverId,
                                  DriverDocumentType type,
                                  LocalDate expiryDate,
                                  String number) {

        DriverDocument doc = new DriverDocument();
        doc.setDriverId(driverId);
        doc.setType(type);
        doc.setExpiryDate(expiryDate);
        doc.setNumber(number);

        return driverDocumentRepository.save(doc);
    }

    public DriverDocument update(Long id,
                                  DriverDocumentType type,
                                  LocalDate expiryDate,
                                  String number) {

        DriverDocument doc = driverDocumentRepository.findById(id).orElseThrow();

        doc.setType(type);
        doc.setExpiryDate(expiryDate);
        doc.setNumber(number);

        return driverDocumentRepository.save(doc);
    }

    public List<DriverDocument> getForDriver(Long driverId) {
        return driverDocumentRepository.findByDriverId(driverId);
    }

    /**
     * Проверява, че документ с това id принадлежи на шофьор от подадената фирма,
     * преди да позволи четене/редакция (server-to-server защита - извикващата
     * страна вече е удостоверена през X-API-KEY, но companyId идва от заявката
     * и трябва да се провери спрямо реалния собственик).
     */
    public DriverDocument getIfBelongsToCompany(Long id, Long companyId) {
        DriverDocument doc = driverDocumentRepository.findById(id).orElse(null);
        if (doc == null) {
            return null;
        }
        Optional<Driver> driver = driverRepository.findByIdAndCompanyId(doc.getDriverId(), companyId);
        return driver.isPresent() ? doc : null;
    }

    public String getStatus(LocalDate expiryDate) {

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) return "EXPIRED";
        if (expiryDate.isBefore(now.plusDays(10))) return "URGENT";
        if (expiryDate.isBefore(now.plusDays(30))) return "WARNING";

        return "OK";
    }
}

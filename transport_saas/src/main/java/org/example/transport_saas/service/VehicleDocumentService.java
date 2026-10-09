package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.DocumentType;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.entity.VehicleDocument;
import org.example.transport_saas.repository.VehicleDocumentRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleDocumentService {

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final VehicleRepository  vehicleRepository;
    private final MessageSource messageSource;

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public List<VehicleDocument> getExpiringDocuments(Long companyId) {

        LocalDate alertDate = LocalDate.now().plusDays(30);

        return vehicleDocumentRepository.findExpiringDocuments(companyId, alertDate);
    }

    public void saveOrUpdate(Long vehicleId,
                             DocumentType type,
                             LocalDate expiryDate) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow();

        Optional<VehicleDocument> existing =
                vehicleDocumentRepository.findByVehicleIdAndType(vehicleId, type);

        if (existing.isPresent()) {
            // update
            VehicleDocument doc = existing.get();
            doc.setExpiryDate(expiryDate);
            vehicleDocumentRepository.save(doc);
        } else {
            // create
            VehicleDocument doc = VehicleDocument.builder()
                    .vehicle(vehicle)
                    .type(type)
                    .expiryDate(expiryDate)
                    .build();

            vehicleDocumentRepository.save(doc);
        }
    }

    public String getStatus(LocalDate expiryDate) {

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) {
            return "EXPIRED";
        }

        if (expiryDate.isBefore(now.plusDays(10))) {
            return "URGENT";
        }

        if (expiryDate.isBefore(now.plusDays(30))) {
            return "WARNING";
        }

        return "OK";
    }

    public String getDisplayType(DocumentType type) {
        return switch (type) {
            case TECHNICAL_INSPECTION -> msg("doc.type.technical_inspection");
            case INSURANCE -> msg("doc.type.insurance");
            case VIGNETTE -> msg("doc.type.vignette");
        };
    }

    public String getDisplayStatus(LocalDate expiryDate) {

        LocalDate now = LocalDate.now();

        if (expiryDate.isBefore(now)) {
            return msg("doc.status.expired");
        }

        if (expiryDate.isBefore(now.plusDays(10))) {
            return msg("doc.status.urgent");
        }

        if (expiryDate.isBefore(now.plusDays(30))) {
            return msg("doc.status.warning");
        }

        return msg("doc.status.ok");
    }

    public Optional<Vehicle> getByVehicle(Long vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public List<VehicleDocument> getAllForCompany(Long companyId) {
        return vehicleDocumentRepository.findByVehicleCompanyId(companyId);
    }

    public void update(Long id, DocumentType type, LocalDate expiryDate) {
        VehicleDocument doc = vehicleDocumentRepository.findById(id).orElseThrow();
        doc.setType(type);
        doc.setExpiryDate(expiryDate);
        vehicleDocumentRepository.save(doc);
    }

    public VehicleDocument getIfBelongsToCompany(Long id, Long companyId) {
        return vehicleDocumentRepository.findByIdAndVehicleCompanyId(id, companyId).orElse(null);
    }
}

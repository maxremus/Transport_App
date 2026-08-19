package org.example.adminservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.example.adminservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Логически JSON бекъп на основните таблици - не разчита на mysqldump
 * (може да липсва в контейнера), а директно чете през JPA и сериализира
 * плоски map-ове (без lazy relations), за да няма проблеми с
 * LazyInitializationException при сериализация.
 */
@Service
@RequiredArgsConstructor
public class BackupService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final DriverRepository driverRepository;
    private final DriverDocumentRepository driverDocumentRepository;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Transactional(readOnly = true)
    public byte[] generateBackup() throws Exception {

        Map<String, Object> backup = new LinkedHashMap<>();
        backup.put("generatedAt", java.time.LocalDateTime.now().toString());

        List<Map<String, Object>> companies = new ArrayList<>();
        companyRepository.findAll().forEach(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("name", c.getName());
            m.put("bulstat", c.getBulstat());
            m.put("active", c.isActive());
            m.put("subscriptionPlan", c.getSubscriptionPlan());
            m.put("subscriptionExpiry", c.getSubscriptionExpiry());
            m.put("trialEndsAt", c.getTrialEndsAt());
            m.put("trialUsed", c.isTrialUsed());
            m.put("stripeSubscriptionId", c.getStripeSubscriptionId());
            companies.add(m);
        });
        backup.put("companies", companies);

        List<Map<String, Object>> users = new ArrayList<>();
        userRepository.findAll().forEach(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("email", u.getEmail());
            m.put("enabled", u.isEnabled());
            m.put("role", u.getRole());
            m.put("companyId", u.getCompany() != null ? u.getCompany().getId() : null);
            m.put("emailNotifications", u.isEmailNotifications());
            // паролата НЕ се включва в бекъпа от съображения за сигурност
            users.add(m);
        });
        backup.put("users", users);

        List<Map<String, Object>> vehicles = new ArrayList<>();
        vehicleRepository.findAll().forEach(v -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", v.getId());
            m.put("registrationNumber", v.getRegistrationNumber());
            m.put("vin", v.getVin());
            m.put("companyId", v.getCompany() != null ? v.getCompany().getId() : null);
            vehicles.add(m);
        });
        backup.put("vehicles", vehicles);

        List<Map<String, Object>> trips = new ArrayList<>();
        tripRepository.findAll().forEach(t -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", t.getId());
            m.put("fromLocation", t.getFromLocation());
            m.put("toLocation", t.getToLocation());
            m.put("revenue", t.getRevenue());
            m.put("fuelCost", t.getFuelCost());
            m.put("tollCost", t.getTollCost());
            m.put("otherCost", t.getOtherCost());
            m.put("tripDate", t.getTripDate());
            m.put("vehicleId", t.getVehicle() != null ? t.getVehicle().getId() : null);
            m.put("companyId", t.getCompany() != null ? t.getCompany().getId() : null);
            trips.add(m);
        });
        backup.put("trips", trips);

        List<Map<String, Object>> vehicleDocs = new ArrayList<>();
        vehicleDocumentRepository.findAll().forEach(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getId());
            m.put("type", d.getType());
            m.put("expiryDate", d.getExpiryDate());
            m.put("vehicleId", d.getVehicle() != null ? d.getVehicle().getId() : null);
            vehicleDocs.add(m);
        });
        backup.put("vehicleDocuments", vehicleDocs);

        List<Map<String, Object>> drivers = new ArrayList<>();
        driverRepository.findAll().forEach(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getId());
            m.put("name", d.getName());
            m.put("phone", d.getPhone());
            m.put("companyId", d.getCompanyId());
            drivers.add(m);
        });
        backup.put("drivers", drivers);

        List<Map<String, Object>> driverDocs = new ArrayList<>();
        driverDocumentRepository.findAll().forEach(d -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", d.getId());
            m.put("type", d.getType());
            m.put("expiryDate", d.getExpiryDate());
            m.put("number", d.getNumber());
            m.put("driverId", d.getDriverId());
            driverDocs.add(m);
        });
        backup.put("driverDocuments", driverDocs);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(backup);
    }
}

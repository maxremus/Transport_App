package org.example.adminservice.service;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.entity.*;
import org.example.adminservice.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDataService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final DriverRepository driverRepository;
    private final AuditLogService auditLogService;

    // ---------- OVERVIEW ----------

    public long totalCompanies() { return companyRepository.count(); }
    public long activeCompanies() {
        return companyRepository.findAll().stream().filter(Company::isActive).count();
    }
    public long totalUsers() { return userRepository.count(); }
    public long totalVehicles() { return vehicleRepository.count(); }
    public long totalTrips() { return tripRepository.count(); }
    public long totalDrivers() { return driverRepository.count(); }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // ---------- COMPANIES ----------

    public Company getCompany(Long id) {
        return companyRepository.findById(id).orElseThrow();
    }

    public void updateCompany(Long id, String name, String bulstat, boolean active,
                               SubscriptionPlan plan, LocalDate expiry) {
        Company company = getCompany(id);

        String before = "plan=" + company.getSubscriptionPlan() + ", active=" + company.isActive()
                + ", expiry=" + company.getSubscriptionExpiry();

        company.setName(name);
        company.setBulstat(bulstat);
        company.setActive(active);
        company.setSubscriptionPlan(plan);
        company.setSubscriptionExpiry(expiry);

        companyRepository.save(company);

        auditLogService.log("UPDATE_COMPANY",
                "companyId=" + id + " | преди: " + before +
                " | след: plan=" + plan + ", active=" + active + ", expiry=" + expiry);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
        auditLogService.log("DELETE_COMPANY", "companyId=" + id);
    }

    // ---------- USERS ----------

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public void setUserEnabled(Long id, boolean enabled) {
        User user = getUser(id);
        user.setEnabled(enabled);
        userRepository.save(user);
        auditLogService.log(enabled ? "ENABLE_USER" : "DISABLE_USER", "userId=" + id + " (" + user.getUsername() + ")");
    }

    // ---------- VEHICLES / TRIPS / DRIVERS (read + delete) ----------

    public List<Vehicle> getAllVehicles() { return vehicleRepository.findAllWithCompany(); }
    public List<Trip> getAllTrips() { return tripRepository.findAllWithCompany(); }
    public List<Driver> getAllDrivers() { return driverRepository.findAll(); }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
        auditLogService.log("DELETE_VEHICLE", "vehicleId=" + id);
    }

    public void deleteTrip(Long id) {
        tripRepository.deleteById(id);
        auditLogService.log("DELETE_TRIP", "tripId=" + id);
    }

    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
        auditLogService.log("DELETE_DRIVER", "driverId=" + id);
    }
}

package org.example.adminservice.service;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.entity.*;
import org.example.adminservice.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return userRepository.findAllWithCompany();
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

    public List<Vehicle> getAllVehicles() { return getAllVehicles("id"); }

    public List<Vehicle> getAllVehicles(String sortBy) {
        List<Vehicle> vehicles = vehicleRepository.findAllWithCompany();
        if ("company".equals(sortBy)) {
            vehicles.sort(Comparator.comparing(
                    (Vehicle v) -> v.getCompany() != null ? v.getCompany().getName() : "",
                    String.CASE_INSENSITIVE_ORDER));
        } else {
            vehicles.sort(Comparator.comparing(Vehicle::getId));
        }
        return vehicles;
    }

    public List<Trip> getAllTrips() { return getAllTrips("id"); }

    public List<Trip> getAllTrips(String sortBy) {
        List<Trip> trips = tripRepository.findAllWithCompany();
        if ("company".equals(sortBy)) {
            trips.sort(Comparator.comparing(
                    (Trip t) -> t.getCompany() != null ? t.getCompany().getName() : "",
                    String.CASE_INSENSITIVE_ORDER));
        } else {
            trips.sort(Comparator.comparing(Trip::getId));
        }
        return trips;
    }
    public List<Driver> getAllDrivers() { return getAllDrivers("id"); }

    public List<Driver> getAllDrivers(String sortBy) {
        List<Driver> drivers = driverRepository.findAll();

        Map<Long, String> companyNames = companyRepository.findAll().stream()
                .collect(Collectors.toMap(Company::getId, Company::getName));

        drivers.forEach(d -> d.setCompanyName(
                d.getCompanyId() != null ? companyNames.getOrDefault(d.getCompanyId(), "—") : "—"));

        if ("company".equals(sortBy)) {
            drivers.sort(Comparator.comparing(Driver::getCompanyName, String.CASE_INSENSITIVE_ORDER));
        } else {
            drivers.sort(Comparator.comparing(Driver::getId));
        }
        return drivers;
    }

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

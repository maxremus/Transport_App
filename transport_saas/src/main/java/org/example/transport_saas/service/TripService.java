package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.Trip;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.repository.TripRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final PlanLimitService planLimitService;

    public List<Trip> getAllForCompany(Long companyId) {
        return tripRepository.findByCompanyId(companyId);
    }

    public void save(Trip trip, Long companyId, Long vehicleId) {

        planLimitService.checkTripLimit(companyId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow();

        trip.setVehicle(vehicle);
        trip.setCompany(
                Company.builder().id(companyId).build()
        );

        if (trip.getTripDate() == null) {
            trip.setTripDate(LocalDate.now());
        }

        tripRepository.save(trip);
    }

    public void delete(Long tripId, Long companyId) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow();

        if (!trip.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Access denied");
        }

        tripRepository.delete(trip);
    }
}

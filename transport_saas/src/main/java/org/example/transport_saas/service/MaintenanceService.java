package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.MaintenanceSchedule;
import org.example.transport_saas.entity.MaintenanceType;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.repository.MaintenanceScheduleRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceScheduleRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public List<MaintenanceSchedule> getAllForCompany(Long companyId) {
        return maintenanceRepository.findAllForCompany(companyId);
    }

    public MaintenanceSchedule getIfBelongsToCompany(Long id, Long companyId) {
        MaintenanceSchedule m = maintenanceRepository.findById(id).orElse(null);
        if (m == null || m.getVehicle() == null || m.getVehicle().getCompany() == null
                || !m.getVehicle().getCompany().getId().equals(companyId)) {
            return null;
        }
        return m;
    }

    public void save(MaintenanceSchedule schedule, Long vehicleId, Long companyId) {
        Vehicle vehicle = vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        schedule.setVehicle(vehicle);

        if (schedule.getLastServiceDate() == null) {
            schedule.setLastServiceDate(LocalDate.now());
        }
        if (schedule.getLastServiceMileage() == null) {
            schedule.setLastServiceMileage(vehicle.getCurrentMileage());
        }

        maintenanceRepository.save(schedule);
    }

    /** Маркира интервала като обслужен днес, при текущия пробег на МПС-то. */
    public void markServiced(Long id, Long companyId) {
        MaintenanceSchedule m = getIfBelongsToCompany(id, companyId);
        if (m == null) {
            throw new RuntimeException("Access denied");
        }
        m.setLastServiceDate(LocalDate.now());
        m.setLastServiceMileage(m.getVehicle().getCurrentMileage());
        maintenanceRepository.save(m);
    }

    public void delete(Long id, Long companyId) {
        MaintenanceSchedule m = getIfBelongsToCompany(id, companyId);
        if (m == null) {
            throw new RuntimeException("Access denied");
        }
        maintenanceRepository.delete(m);
    }

    public LocalDate getNextServiceDate(MaintenanceSchedule m) {
        if (m.getIntervalMonths() == null || m.getLastServiceDate() == null) {
            return null;
        }
        return m.getLastServiceDate().plusMonths(m.getIntervalMonths());
    }

    public Integer getNextServiceMileage(MaintenanceSchedule m) {
        if (m.getIntervalKm() == null || m.getLastServiceMileage() == null) {
            return null;
        }
        return m.getLastServiceMileage() + m.getIntervalKm();
    }

    /** ПРОСРОЧЕН / СКОРО / ОК - взима предвид и дата, и пробег, каквото дойде първо. */
    public String getDisplayStatus(MaintenanceSchedule m) {
        LocalDate nextDate = getNextServiceDate(m);
        Integer nextKm = getNextServiceMileage(m);
        Integer currentMileage = m.getVehicle() != null ? m.getVehicle().getCurrentMileage() : null;

        boolean overdueByDate = nextDate != null && !nextDate.isAfter(LocalDate.now());
        boolean overdueByKm = nextKm != null && currentMileage != null && currentMileage >= nextKm;

        if (overdueByDate || overdueByKm) {
            return "ПРОСРОЧЕН";
        }

        boolean soonByDate = nextDate != null && nextDate.isBefore(LocalDate.now().plusDays(30));
        boolean soonByKm = nextKm != null && currentMileage != null && currentMileage >= nextKm - 1000;

        if (soonByDate || soonByKm) {
            return "СКОРО";
        }

        return "ОК";
    }

    public String getDisplayType(MaintenanceType type) {
        return switch (type) {
            case OIL_CHANGE -> "Смяна на масло";
            case TIRES -> "Гуми";
            case BRAKES -> "Спирачки";
            case TECHNICAL_INSPECTION -> "Годишен технически преглед";
            case OTHER -> "Друго";
        };
    }
}

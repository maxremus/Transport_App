package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.MaintenanceSchedule;
import org.example.transport_saas.entity.MaintenanceType;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.repository.MaintenanceScheduleRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceScheduleRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final MessageSource messageSource;

    private String msg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

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

    /** Суров, locale-независим статус - за CSS/логика в шаблоните (НЕ за показване). */
    public String getStatus(MaintenanceSchedule m) {
        LocalDate nextDate = getNextServiceDate(m);
        Integer nextKm = getNextServiceMileage(m);
        Integer currentMileage = m.getVehicle() != null ? m.getVehicle().getCurrentMileage() : null;

        boolean overdueByDate = nextDate != null && !nextDate.isAfter(LocalDate.now());
        boolean overdueByKm = nextKm != null && currentMileage != null && currentMileage >= nextKm;

        if (overdueByDate || overdueByKm) {
            return "OVERDUE";
        }

        boolean soonByDate = nextDate != null && nextDate.isBefore(LocalDate.now().plusDays(30));
        boolean soonByKm = nextKm != null && currentMileage != null && currentMileage >= nextKm - 1000;

        if (soonByDate || soonByKm) {
            return "SOON";
        }

        return "OK";
    }

    /** ПРОСРОЧЕН / СКОРО / ОК (превежда се) - само за показване в UI. */
    public String getDisplayStatus(MaintenanceSchedule m) {
        return switch (getStatus(m)) {
            case "OVERDUE" -> msg("maintenance.status.overdue");
            case "SOON" -> msg("maintenance.status.soon");
            default -> msg("maintenance.status.ok");
        };
    }

    public String getDisplayType(MaintenanceType type) {
        return switch (type) {
            case OIL_CHANGE -> msg("maintenance.type.oil_change");
            case TIRES -> msg("maintenance.type.tires");
            case BRAKES -> msg("maintenance.type.brakes");
            case TECHNICAL_INSPECTION -> msg("maintenance.type.technical_inspection");
            case OTHER -> msg("maintenance.type.other");
        };
    }
}

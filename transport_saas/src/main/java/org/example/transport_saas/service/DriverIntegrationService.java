package org.example.transport_saas.service;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.entity.Driver;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Управлява шофьорите изцяло локално в transport_saas.
 *
 * По-рано createDriver()/createDoc() правеха допълнително HTTP извикване
 * (Feign) към driver-service, за да дублират записа и там. Това чупеше с
 * грешка (несъвпадащи ID-та между двете отделни бази, различни типове на
 * полетата), докато локалният запис вече беше минал успешно - оттам
 * "дава грешка, но пак записва". Данните от driver-service така или иначе
 * никога не се четяха обратно никъде в интерфейса, затова целият remote
 * запис е излишен и е премахнат.
 */
@Service
@RequiredArgsConstructor
public class DriverIntegrationService {

    private final DriverService driverService;

    public List<Driver> getAllDrivers() {
        return driverService.getDriversByCompany();
    }

    public void createDriver(String name, String phone) {
        driverService.createDriver(name, phone);
    }

    public Driver getDriverIfBelongsToCompany(Long driverId, Long companyId) {
        return driverService.getDriverIfBelongsToCompany(driverId, companyId);
    }
}

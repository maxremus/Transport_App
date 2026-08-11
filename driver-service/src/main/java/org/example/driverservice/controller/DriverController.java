package org.example.driverservice.controller;

import org.example.driverservice.entity.Driver;
import org.example.driverservice.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverRepository driverRepository;

    @Value("${app.internal-key}")
    private String internalKey;

    public DriverController(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    private void validateKey(String apiKey) {
        if (!internalKey.equals(apiKey)) {
            throw new RuntimeException("Unauthorized");
        }
    }

    @GetMapping
    public List<Driver> getAllDrivers(@RequestParam Long companyId,
                                       @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);
        return driverRepository.findByCompanyId(companyId);
    }

    @GetMapping("/{id}")
    public Driver getDriver(@PathVariable Long id,
                             @RequestParam Long companyId,
                             @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);
        return driverRepository.findByIdAndCompanyId(id, companyId).orElseThrow();
    }

    @PostMapping
    public Driver createDriver(@RequestBody Driver driver,
                                @RequestHeader("X-API-KEY") String apiKey) {
        validateKey(apiKey);
        return driverRepository.save(driver);
    }
}

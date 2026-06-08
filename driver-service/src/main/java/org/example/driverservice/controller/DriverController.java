package org.example.driverservice.controller;

import org.example.driverservice.entity.Driver;
import org.example.driverservice.repository.DriverRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverRepository driverRepository;

    public DriverController(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @PostMapping
    public Driver createDriver(@RequestBody Driver driver) {
        return driverRepository.save(driver);
    }
}

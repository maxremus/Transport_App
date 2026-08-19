package org.example.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.service.AdminDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DataController {

    private final AdminDataService adminDataService;

    @GetMapping("/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("vehicles", adminDataService.getAllVehicles());
        return "vehicles";
    }

    @PostMapping("/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable Long id) {
        adminDataService.deleteVehicle(id);
        return "redirect:/vehicles";
    }

    @GetMapping("/trips")
    public String trips(Model model) {
        model.addAttribute("trips", adminDataService.getAllTrips());
        return "trips";
    }

    @PostMapping("/trips/{id}/delete")
    public String deleteTrip(@PathVariable Long id) {
        adminDataService.deleteTrip(id);
        return "redirect:/trips";
    }

    @GetMapping("/drivers")
    public String drivers(Model model) {
        model.addAttribute("drivers", adminDataService.getAllDrivers());
        return "drivers";
    }

    @PostMapping("/drivers/{id}/delete")
    public String deleteDriver(@PathVariable Long id) {
        adminDataService.deleteDriver(id);
        return "redirect:/drivers";
    }
}

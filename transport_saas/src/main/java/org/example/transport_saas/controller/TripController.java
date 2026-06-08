package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Trip;
import org.example.transport_saas.service.TripService;
import org.example.transport_saas.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final VehicleService vehicleService;

    @GetMapping
    public String list(Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        model.addAttribute("trips",
                tripService.getAllForCompany(companyId));

        model.addAttribute("vehicles",
                vehicleService.getAllForCompany(companyId));

        model.addAttribute("trip", new Trip());

        return "trips";
    }

    @PostMapping
    public String add(@ModelAttribute Trip trip,
                      @RequestParam Long vehicleId) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        tripService.save(trip, companyId, vehicleId);

        return "redirect:/trips";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        tripService.delete(id, companyId);

        return "redirect:/trips";
    }
}

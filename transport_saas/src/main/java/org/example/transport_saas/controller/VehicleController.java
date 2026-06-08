package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.service.VehicleDocumentService;
import org.example.transport_saas.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleDocumentService vehicleDocumentService;

    @GetMapping
    public String list(Model model) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        model.addAttribute("vehicles",
                vehicleService.getAllForCompany(companyId));
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("documents", vehicleDocumentService.getAllForCompany(companyId));
        return "vehicles";
    }

    @PostMapping
    public String add(@ModelAttribute Vehicle vehicle) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        vehicleService.save(vehicle, companyId);
        return "redirect:/vehicles";
    }
}

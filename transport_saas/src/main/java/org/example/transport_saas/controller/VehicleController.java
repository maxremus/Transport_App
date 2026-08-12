package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Vehicle;
import org.example.transport_saas.service.VehicleDocumentService;
import org.example.transport_saas.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleDocumentService vehicleDocumentService;

    @GetMapping
    public String list(@RequestParam(required = false) Long editId,
                        @RequestParam(required = false) Long editDocId,
                        Model model) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        model.addAttribute("vehicles",
                vehicleService.getAllForCompany(companyId));
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("documents", vehicleDocumentService.getAllForCompany(companyId));

        model.addAttribute("editVehicle",
                editId != null ? vehicleService.getVehicleIfBelongsToCompany(editId, companyId) : null);

        model.addAttribute("editDoc",
                editDocId != null ? vehicleDocumentService.getIfBelongsToCompany(editDocId, companyId) : null);

        return "vehicles";
    }

    @PostMapping
    public String add(@ModelAttribute Vehicle vehicle) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        vehicleService.save(vehicle, companyId);
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Vehicle vehicle) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        vehicleService.update(id, companyId, vehicle);
        return "redirect:/vehicles";
    }
}

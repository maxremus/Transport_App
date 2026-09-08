package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.MaintenanceSchedule;
import org.example.transport_saas.service.MaintenanceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    public String add(@ModelAttribute MaintenanceSchedule schedule,
                       @RequestParam Long vehicleId) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        maintenanceService.save(schedule, vehicleId, companyId);
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/serviced")
    public String markServiced(@PathVariable Long id) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        maintenanceService.markServiced(id, companyId);
        return "redirect:/vehicles";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        maintenanceService.delete(id, companyId);
        return "redirect:/vehicles";
    }
}

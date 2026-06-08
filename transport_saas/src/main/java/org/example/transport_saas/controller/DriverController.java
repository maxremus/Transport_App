package org.example.transport_saas.controller;

import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.service.DriverIntegrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DriverController {

    private final DriverIntegrationService driverIntegrationService;

    public DriverController(DriverIntegrationService driverIntegrationService) {
        this.driverIntegrationService = driverIntegrationService;
    }

    @GetMapping("/driver-add")
    public String addDriverPage(Model model) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        model.addAttribute("companyId", companyId);
        return "driver-add";
    }

    @PostMapping("/drivers/add")
    public String addDriver(String name, String phone) {
        driverIntegrationService.createDriver(name, phone);
        return "redirect:/driver";
    }

    @GetMapping("/driver")
    public String driverPage(Model model) {

        model.addAttribute("drivers", driverIntegrationService.getAllDrivers());

        return "driver";
    }

    @GetMapping("/driver-documents")
    public String documentsPage(@RequestParam(required = false) Long driverId, Model model) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        
        // Verify driver belongs to current company
        if (driverId != null) {
            var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
            if (driver == null) {
                return "redirect:/driver"; // Unauthorized access attempt
            }
        }
        
        model.addAttribute("driverId", driverId);
        model.addAttribute("companyId", companyId);
        return "driver-documents";
    }

    @PostMapping("/driver-documents")
    public String save(
            @RequestParam Long driverId,
            @RequestParam String type,
            @RequestParam String number,
            @RequestParam String expiryDate
    ) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        
        // Verify driver belongs to current company
        var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
        if (driver == null) {
            return "redirect:/driver"; // Unauthorized access attempt
        }

        driverIntegrationService.createDoc(driverId, type, number, expiryDate);

        return "redirect:/driver";
    }


}

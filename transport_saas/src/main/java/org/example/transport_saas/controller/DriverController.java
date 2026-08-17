package org.example.transport_saas.controller;

import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.DriverDocumentType;
import org.example.transport_saas.service.DriverIntegrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public String documentsPage(@RequestParam(required = false) Long driverId,
                                 @RequestParam(required = false) Long editId,
                                 Model model) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        if (driverId != null) {
            var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
            if (driver == null) {
                return "redirect:/driver"; // Unauthorized access attempt
            }

            model.addAttribute("documents", driverIntegrationService.getDocumentsForDriver(driverId));
            model.addAttribute("driverName", driver.getName());
        }

        if (editId != null) {
            var doc = driverIntegrationService.getDocumentsForDriver(driverId).stream()
                    .filter(d -> d.getId().equals(editId))
                    .findFirst()
                    .orElse(null);

            if (doc == null) {
                return "redirect:/driver-documents?driverId=" + driverId;
            }
            model.addAttribute("editDoc", doc);
        }

        model.addAttribute("driverId", driverId);
        model.addAttribute("companyId", companyId);
        return "driver-documents";
    }

    @PostMapping("/driver-documents")
    public String save(
            @RequestParam Long driverId,
            @RequestParam DriverDocumentType type,
            @RequestParam String number,
            @RequestParam LocalDate expiryDate
    ) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
        if (driver == null) {
            return "redirect:/driver"; // Unauthorized access attempt
        }

        driverIntegrationService.createDocument(driverId, type, number, expiryDate);

        return "redirect:/driver-documents?driverId=" + driverId;
    }

    @PostMapping("/driver-documents/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam Long driverId,
            @RequestParam DriverDocumentType type,
            @RequestParam String number,
            @RequestParam LocalDate expiryDate
    ) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        // Проверка, че документът е за шофьор от текущата фирма
        var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
        if (driver == null) {
            return "redirect:/driver";
        }

        driverIntegrationService.updateDocument(id, type, number, expiryDate);

        return "redirect:/driver-documents?driverId=" + driverId;
    }

}

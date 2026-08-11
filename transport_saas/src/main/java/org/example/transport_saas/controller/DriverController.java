package org.example.transport_saas.controller;

import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.DriverDocumentType;
import org.example.transport_saas.service.DriverDocumentService;
import org.example.transport_saas.service.DriverIntegrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class DriverController {

    private final DriverIntegrationService driverIntegrationService;
    private final DriverDocumentService driverDocumentService;

    public DriverController(DriverIntegrationService driverIntegrationService,
                             DriverDocumentService driverDocumentService) {
        this.driverIntegrationService = driverIntegrationService;
        this.driverDocumentService = driverDocumentService;
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

        // Verify driver belongs to current company
        if (driverId != null) {
            var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
            if (driver == null) {
                return "redirect:/driver"; // Unauthorized access attempt
            }

            model.addAttribute("documents", driverDocumentService.getForDriver(driverId));
        }

        // Ако е избрана редакция - зареждаме документа за предпопълване на формата
        if (editId != null) {
            var doc = driverDocumentService.getIfBelongsToCompany(editId, companyId);
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

        // Verify driver belongs to current company
        var driver = driverIntegrationService.getDriverIfBelongsToCompany(driverId, companyId);
        if (driver == null) {
            return "redirect:/driver"; // Unauthorized access attempt
        }

        driverDocumentService.create(driverId, type, number, expiryDate);

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

        // Проверка, че документът принадлежи на текуща фирма
        var doc = driverDocumentService.getIfBelongsToCompany(id, companyId);
        if (doc == null) {
            return "redirect:/driver";
        }

        driverDocumentService.update(id, type, number, expiryDate);

        return "redirect:/driver-documents?driverId=" + driverId;
    }

}

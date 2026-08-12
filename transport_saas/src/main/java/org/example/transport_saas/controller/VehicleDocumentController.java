package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.DocumentType;
import org.example.transport_saas.service.VehicleDocumentService;
import org.example.transport_saas.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/documents")
public class VehicleDocumentController {

    private final VehicleDocumentService vehicleDocumentService;
    private final VehicleService vehicleService;

    @PostMapping
    public String save(Long vehicleId,
                       DocumentType type,
                       LocalDate expiryDate) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        // Verify vehicle belongs to current company
        var vehicle = vehicleService.getVehicleIfBelongsToCompany(vehicleId, companyId);
        if (vehicle == null) {
            return "redirect:/vehicles"; // Unauthorized access attempt
        }

        vehicleDocumentService.saveOrUpdate(vehicleId, type, expiryDate);

        return "redirect:/vehicles";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         DocumentType type,
                         LocalDate expiryDate) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        // Verify document belongs to current company
        var doc = vehicleDocumentService.getIfBelongsToCompany(id, companyId);
        if (doc == null) {
            return "redirect:/vehicles"; // Unauthorized access attempt
        }

        vehicleDocumentService.update(id, type, expiryDate);

        return "redirect:/vehicles";
    }
}

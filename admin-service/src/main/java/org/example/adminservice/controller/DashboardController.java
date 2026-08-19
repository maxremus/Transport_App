package org.example.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.service.AdminDataService;
import org.example.adminservice.service.AuditLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AdminDataService adminDataService;
    private final AuditLogService auditLogService;

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute("totalCompanies", adminDataService.totalCompanies());
        model.addAttribute("activeCompanies", adminDataService.activeCompanies());
        model.addAttribute("totalUsers", adminDataService.totalUsers());
        model.addAttribute("totalVehicles", adminDataService.totalVehicles());
        model.addAttribute("totalTrips", adminDataService.totalTrips());
        model.addAttribute("totalDrivers", adminDataService.totalDrivers());
        model.addAttribute("recentLogs", auditLogService.getRecent(15));

        return "dashboard";
    }
}

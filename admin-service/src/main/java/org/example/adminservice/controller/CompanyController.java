package org.example.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.entity.SubscriptionPlan;
import org.example.adminservice.service.AdminDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private final AdminDataService adminDataService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("companies", adminDataService.getAllCompanies());
        return "companies";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("company", adminDataService.getCompany(id));
        model.addAttribute("plans", SubscriptionPlan.values());
        return "company-edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @RequestParam String name,
                          @RequestParam(required = false) String bulstat,
                          @RequestParam(required = false) boolean active,
                          @RequestParam SubscriptionPlan subscriptionPlan,
                          @RequestParam(required = false) LocalDate subscriptionExpiry) {

        adminDataService.updateCompany(id, name, bulstat, active, subscriptionPlan, subscriptionExpiry);
        return "redirect:/companies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminDataService.deleteCompany(id);
        return "redirect:/companies";
    }
}

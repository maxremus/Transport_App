package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.repository.CompanyRepository;
import org.example.transport_saas.repository.TripRepository;
import org.example.transport_saas.repository.VehicleRepository;
import org.example.transport_saas.service.PaymentIntegrationService;
import org.example.transport_saas.service.PlanLimitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/upgrade")
@RequiredArgsConstructor
public class UpgradeController {

    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final PlanLimitService planLimitService;
    private final PaymentIntegrationService paymentIntegrationService;

    @GetMapping("/{plan}")
    public String upgrade(@PathVariable String plan) {

        String url = paymentIntegrationService.createCheckout(plan);

        return "redirect:" + url;
    }


    @GetMapping
    public String upgradePage(Model model) {

        Long companyId = SecurityUtils.getCurrentCompanyId();

        Company company = companyRepository.findById(companyId).orElseThrow();

        long vehicleCount = vehicleRepository.countByCompanyId(companyId);
        long tripCount = tripRepository.countByCompanyId(companyId);

        int vehicleLimit = planLimitService.getVehicleLimit(companyId);
        int tripLimit = planLimitService.getTripLimit(companyId);

        model.addAttribute("currentPlan", company.getSubscriptionPlan());
        model.addAttribute("vehicleCount", vehicleCount);
        model.addAttribute("tripCount", tripCount);
        model.addAttribute("vehicleLimit", vehicleLimit);
        model.addAttribute("tripLimit", tripLimit);

        System.out.println("PLAN FROM DB: " + company.getSubscriptionPlan());
        System.out.println("VEHICLE LIMIT: " + vehicleLimit);

        return "upgrade";
    }
}

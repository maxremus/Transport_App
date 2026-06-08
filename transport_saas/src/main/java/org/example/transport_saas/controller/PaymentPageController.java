package org.example.transport_saas.controller;

import org.example.transport_saas.entity.Company;
import org.example.transport_saas.repository.CompanyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentPageController {

    private final CompanyRepository companyRepository;

    public PaymentPageController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping("/success")
    public String success() {
        return "redirect:/upgrade?success=true";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "payment-cancel";
    }


}

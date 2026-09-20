package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.service.CompanyService;
import org.example.transport_saas.service.SupportMailService;
import org.example.transport_saas.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/**
 * Страница "Помощ" - позволява на потребителя да прати съобщение
 * директно до администратора (admintransportapp@gmail.com).
 */
@Controller
@RequiredArgsConstructor
public class SupportController {

    private final UserService userService;
    private final CompanyService companyService;
    private final SupportMailService supportMailService;

    @GetMapping("/support")
    public String supportPage(Model model, Principal principal) {

        User user = userService.findByUsername(principal.getName());
        Long companyId = SecurityUtils.getCurrentCompanyId();
        Company company = companyService.getById(companyId);

        model.addAttribute("user", user);
        model.addAttribute("company", company);

        return "support";
    }

    @PostMapping("/support")
    public String sendMessage(@RequestParam String subject,
                               @RequestParam String message,
                               Principal principal) {

        User user = userService.findByUsername(principal.getName());
        Long companyId = SecurityUtils.getCurrentCompanyId();
        Company company = companyService.getById(companyId);

        supportMailService.sendToAdmin(company, user, subject, message);

        return "redirect:/support?sent";
    }
}

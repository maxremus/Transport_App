package org.example.transport_saas.controller;

import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.service.CompanyService;
import org.example.transport_saas.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class SettingsController {

    private final UserService userService;
    private final CompanyService companyService;

    public SettingsController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {

        // principal.getName() връща USERNAME (така работи Spring Security тук),
        // не email - затова по-рано търсенето по findByEmail никога не намираше
        // потребителя и страницата винаги пренасочваше обратно към /login.
        User user = userService.findByUsername(principal.getName());
        Long companyId = SecurityUtils.getCurrentCompanyId();
        Company company = companyService.getById(companyId);

        model.addAttribute("user", user);
        model.addAttribute("company", company);
        model.addAttribute("plan", company.getSubscriptionPlan());
        model.addAttribute("active", company.isActive());
        model.addAttribute("expiry", company.getSubscriptionExpiry());

        return "settings";
    }

    @PostMapping("/settings/notifications")
    public String updateNotifications(@RequestParam(required = false) boolean emailNotifications,
                                       Principal principal) {

        User user = userService.findByUsername(principal.getName());
        user.setEmailNotifications(emailNotifications);
        userService.save(user);

        return "redirect:/settings?saved";
    }

    @PostMapping("/settings/company")
    public String updateCompany(@RequestParam String name,
                                 @RequestParam(required = false) String bulstat) {

        Long companyId = SecurityUtils.getCurrentCompanyId();
        companyService.updateProfile(companyId, name, bulstat);

        return "redirect:/settings?saved";
    }

    @PostMapping("/settings/password")
    public String updatePassword(@RequestParam String currentPassword,
                                  @RequestParam String newPassword,
                                  @RequestParam String confirmPassword,
                                  Principal principal,
                                  Model model) {

        User user = userService.findByUsername(principal.getName());

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/settings?passwordError=mismatch";
        }

        boolean changed = userService.changePassword(user, currentPassword, newPassword);

        if (!changed) {
            return "redirect:/settings?passwordError=wrong";
        }

        return "redirect:/settings?passwordChanged";
    }
}

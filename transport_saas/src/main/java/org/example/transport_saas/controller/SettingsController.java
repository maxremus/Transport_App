package org.example.transport_saas.controller;

import org.example.transport_saas.auth.SecurityUtils;
import org.example.transport_saas.entity.Company;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.service.CompanyService;
import org.example.transport_saas.service.NotificationEmailService;
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
    private final NotificationEmailService notificationEmailService;

    public SettingsController(UserService userService, CompanyService companyService,
                               NotificationEmailService notificationEmailService) {
        this.userService = userService;
        this.companyService = companyService;
        this.notificationEmailService = notificationEmailService;
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

    @PostMapping("/settings/notifications/test")
    public String sendTestNotifications(Principal principal) {

        Long companyId = SecurityUtils.getCurrentCompanyId();
        boolean sent = notificationEmailService.sendNowForCompany(companyId);

        return "redirect:/settings?" + (sent ? "notificationSent" : "notificationEmpty");
    }

    @PostMapping("/settings/company")
    public String updateCompany(@RequestParam String name,
                                 @RequestParam(required = false) String bulstat,
                                 @RequestParam(required = false) String address,
                                 @RequestParam(required = false) String iban,
                                 @RequestParam(required = false) String mol,
                                 @RequestParam(required = false) String vatNumber,
                                 @RequestParam(required = false) boolean vatRegistered) {

        Long companyId = SecurityUtils.getCurrentCompanyId();
        companyService.updateProfile(companyId, name, bulstat, address, iban, mol, vatNumber, vatRegistered);

        return "redirect:/settings?saved";
    }

    @PostMapping("/settings/invoice-numbering")
    public String updateInvoiceNumbering(@RequestParam long nextInvoiceNumber) {

        Long companyId = SecurityUtils.getCurrentCompanyId();
        companyService.updateNextInvoiceNumber(companyId, Math.max(1L, nextInvoiceNumber));

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

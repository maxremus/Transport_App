package org.example.transport_saas.controller;

import org.example.transport_saas.entity.Subscription;
import org.example.transport_saas.entity.User;
import org.example.transport_saas.service.SubscriptionService;
import org.example.transport_saas.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class SettingsController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public SettingsController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {

        try {
            User user = userService.findByEmail(principal.getName());
            Optional<Subscription> sub = subscriptionService.getActive(user);

            model.addAttribute("user", user);
            model.addAttribute("active", sub.isPresent());
            model.addAttribute("endDate",
                    sub.map(Subscription::getEndDate).orElse(null));
        } catch (RuntimeException e) {
            return "redirect:/login";
        }

        return "settings";
    }

    @PostMapping("/settings")
    public String updateSettings(@RequestParam(required = false) boolean emailNotifications,
                                 Principal principal) {


        try {
            User user = userService.findByEmail(principal.getName());
            user.setEmailNotifications(emailNotifications);
            userService.save(user);
        } catch (RuntimeException e) {
            return "redirect:/login";
        }

        return "redirect:/settings";
    }
}

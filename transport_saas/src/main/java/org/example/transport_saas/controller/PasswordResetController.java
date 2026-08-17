package org.example.transport_saas.controller;

import lombok.RequiredArgsConstructor;
import org.example.transport_saas.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        passwordResetService.requestReset(email);
        model.addAttribute("sent", true);
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {

        boolean valid = passwordResetService.isTokenValid(token);

        model.addAttribute("token", token);
        model.addAttribute("valid", valid);

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("valid", true);
            model.addAttribute("error", "Паролите не съвпадат.");
            return "reset-password";
        }

        boolean success = passwordResetService.resetPassword(token, newPassword);

        if (!success) {
            model.addAttribute("token", token);
            model.addAttribute("valid", false);
            return "reset-password";
        }

        return "redirect:/login?passwordReset";
    }
}

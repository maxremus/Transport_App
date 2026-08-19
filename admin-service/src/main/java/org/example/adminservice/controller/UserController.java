package org.example.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.service.AdminDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final AdminDataService adminDataService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", adminDataService.getAllUsers());
        return "users";
    }

    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id) {
        adminDataService.setUserEnabled(id, true);
        return "redirect:/users";
    }

    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id) {
        adminDataService.setUserEnabled(id, false);
        return "redirect:/users";
    }
}

package org.example.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.service.AuditLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/audit-log")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("logs", auditLogService.getRecent(200));
        return "audit-log";
    }
}

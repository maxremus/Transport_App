package org.example.adminservice.service;

import lombok.RequiredArgsConstructor;
import org.example.adminservice.entity.AdminAuditLog;
import org.example.adminservice.repository.AdminAuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AdminAuditLogRepository auditLogRepository;

    public void log(String action, String details) {

        String admin = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "system";

        AdminAuditLog entry = AdminAuditLog.builder()
                .timestamp(LocalDateTime.now())
                .adminUsername(admin)
                .action(action)
                .details(details)
                .build();

        auditLogRepository.save(entry);
    }

    public List<AdminAuditLog> getRecent(int limit) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, limit));
    }
}

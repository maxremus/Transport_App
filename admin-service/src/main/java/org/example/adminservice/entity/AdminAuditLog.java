package org.example.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Уникално име на таблицата (as_ = admin-service), за да не се сблъска с
// нищо от съществуващата споделена схема на другите услуги.
@Entity
@Table(name = "as_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String adminUsername;

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String details;
}

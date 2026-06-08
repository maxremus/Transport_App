package org.example.transport_saas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private SubscriptionPlan plan; // PRO / PREMIUM

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean active;

    @ManyToOne
    private User user;
}

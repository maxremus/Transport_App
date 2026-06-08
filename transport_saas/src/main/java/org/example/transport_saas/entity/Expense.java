package org.example.transport_saas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;
}

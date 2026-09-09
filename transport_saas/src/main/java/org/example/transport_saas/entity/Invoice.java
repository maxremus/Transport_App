package org.example.transport_saas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // напр. "2026-0001" - формира се от InvoiceService при създаване
    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate issueDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // ставка на ДДС в проценти (напр. 20.00) - 0, ако фирмата не е
    // регистрирана по ДДС към датата на издаване
    @Column(nullable = false)
    private BigDecimal vatRate = BigDecimal.ZERO;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Builder.Default
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    /** Сума на начисления ДДС. */
    public BigDecimal getVatAmount() {
        BigDecimal base = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal rate = vatRate != null ? vatRate : BigDecimal.ZERO;
        return base.multiply(rate).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    /** Крайна сума за плащане (данъчна основа + ДДС). */
    public BigDecimal getGrandTotal() {
        BigDecimal base = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        return base.add(getVatAmount());
    }
}

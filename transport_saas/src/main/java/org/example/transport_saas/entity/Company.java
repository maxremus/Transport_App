package org.example.transport_saas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String bulstat;

    // данни за фактуриране - показват се в PDF фактурата като данни
    // на доставчика
    private String address;

    private String iban;

    private String mol; // Материално отговорно лице

    private String vatNumber; // ДДС номер (ако фирмата е регистрирана по ДДС)

    @Column(nullable = false)
    private boolean vatRegistered = false;

    @Column(nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    private SubscriptionPlan subscriptionPlan;

    private LocalDate subscriptionExpiry;

    private LocalDate trialEndsAt;

    private boolean trialUsed;

    private String stripeSubscriptionId;

    // Следващият номер, който ще се използва за нова фактура. Потребителят
    // може да го зададе от Настройки, за да "продължи" номерацията от
    // предишна система (напр. ако досега е стигнал до 00000042, задава 43).
    // Nullable нарочно (не primitive long) - за да не гръмне ALTER TABLE
    // при вече съществуващи фирми в базата, когато Hibernate добави
    // колоната с ddl-auto=update. getNextInvoiceNumberOrDefault() покрива
    // случая, в който стойността все още е null за стари записи.
    private Long nextInvoiceNumber;

    public long getNextInvoiceNumberOrDefault() {
        return nextInvoiceNumber != null ? nextInvoiceNumber : 1L;
    }

}

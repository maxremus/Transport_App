package org.example.transport_saas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * График за поддръжка на МПС (масло, гуми, спирачки, ГТП и т.н.).
 * Следващата дата за обслужване се смята по интервал от време
 * (intervalMonths) и/или по пробег (intervalKm) - което дойде първо.
 */
@Entity
@Table(name = "maintenance_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;

    // свободен текст, ползва се основно когато type = OTHER
    private String description;

    // интервал в месеци между две обслужвания (незадължителен)
    private Integer intervalMonths;

    // интервал в километри между две обслужвания (незадължителен)
    private Integer intervalKm;

    @Column(nullable = false)
    private LocalDate lastServiceDate;

    private Integer lastServiceMileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
}

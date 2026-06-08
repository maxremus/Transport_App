package org.example.driverservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocument {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private DriverDocumentType type;

    private LocalDate expiryDate;

    private String documentNumber;

    private Long driverId;
}

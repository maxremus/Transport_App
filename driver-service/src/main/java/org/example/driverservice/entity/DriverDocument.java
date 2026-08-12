package org.example.driverservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ds_driver_documents")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocument {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DriverDocumentType type;

    private LocalDate expiryDate;

    private String number;

    private Long driverId;
}

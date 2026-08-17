package org.example.transport_saas.DTO;

import lombok.Data;
import org.example.transport_saas.entity.DriverDocumentType;

import java.time.LocalDate;

@Data
public class DriverDocumentRequestDTO {

    private Long id;
    private Long driverId;
    private DriverDocumentType type;
    private LocalDate expiryDate;
    private String number;

    // Само за показване в таблото - попълва се client-side след join с шофьора,
    // не се праща при create/update заявки
    private String driverName;
}

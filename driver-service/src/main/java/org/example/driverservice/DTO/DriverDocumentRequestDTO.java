package org.example.driverservice.DTO;

import lombok.Data;
import org.example.driverservice.entity.DriverDocumentType;

import java.time.LocalDate;

@Data
public class DriverDocumentRequestDTO {

    private Long driverId;

    private DriverDocumentType type;

    private LocalDate expiryDate;

    private String number;
}

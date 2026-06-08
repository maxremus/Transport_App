package org.example.transport_saas.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverDocumentRequestDTO {

    private Long driverId;
    private String type;
    private String expiryDate;
    private String number;
}

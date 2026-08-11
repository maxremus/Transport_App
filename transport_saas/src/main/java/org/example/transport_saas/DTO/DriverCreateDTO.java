package org.example.transport_saas.DTO;

import lombok.Data;

@Data
public class DriverCreateDTO {

    private String name;
    private String phone;
    private Long companyId;
}

package org.example.transport_saas.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String companyName;
    private String username;
    private String password;
}

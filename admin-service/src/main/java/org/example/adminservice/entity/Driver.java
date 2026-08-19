package org.example.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ds_drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private Long companyId;
}

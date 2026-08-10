package org.example.transport_saas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String phone;
    private String licenseNumber;

    private boolean available;

    @ManyToOne
    private Company company;

    @OneToOne
    private Vehicle vehicle;

}

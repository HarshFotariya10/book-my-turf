package com.bookmyturf.entity;

import com.bookmyturf.constraints.Roles;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String contact;
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private Roles role;
}

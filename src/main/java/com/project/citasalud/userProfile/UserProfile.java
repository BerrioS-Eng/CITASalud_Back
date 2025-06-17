package com.project.citasalud.userProfile;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String department;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String numberPhone;

}

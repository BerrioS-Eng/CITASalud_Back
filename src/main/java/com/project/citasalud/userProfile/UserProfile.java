package com.project.citasalud.userProfile;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

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

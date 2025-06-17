package com.project.citasalud.userProfile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.citasalud.userAuth.UserAuth;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userAuth"})
@EqualsAndHashCode(exclude = {"userAuth"})
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

    @OneToOne(mappedBy = "userProfile", fetch = FetchType.LAZY)
    @JsonBackReference
    private UserAuth userAuth;
}

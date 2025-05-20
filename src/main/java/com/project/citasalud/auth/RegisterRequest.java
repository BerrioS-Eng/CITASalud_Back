package com.project.citasalud.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String username;
    String password;
    String firstName;
    String lastName;
    String department;
    String city;
    String address;
    String email;
    String numberPhone;
}

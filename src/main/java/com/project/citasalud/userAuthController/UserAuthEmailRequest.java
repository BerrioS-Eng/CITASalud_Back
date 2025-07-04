package com.project.citasalud.userAuthController;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthEmailRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    String email;
}

package com.project.citasalud.userProfileController;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    @NotBlank(message = "First name is required")
    String firstName;
    @NotBlank(message = "Last name is required")
    String lastName;
    @NotBlank(message = "Department is required")
    String department;
    @NotBlank(message = "City is required")
    String city;
    @NotBlank(message = "Address is required")
    String address;
    @NotBlank(message = "Number phone is required")
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Number phone must contain only digits")
    String numberPhone;
}

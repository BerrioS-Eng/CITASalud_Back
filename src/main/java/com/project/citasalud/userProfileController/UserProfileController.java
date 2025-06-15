package com.project.citasalud.userProfileController;

import com.project.citasalud.userProfile.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/edit-profile")
    public ResponseEntity<UserProfileDTO> editProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader,
            @Valid @RequestBody UserProfileDTO request) {
        return ResponseEntity.ok(userProfileService.editProfile(authHeader, request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return ResponseEntity.ok(userProfileService.getProfile(authHeader));
    }
}

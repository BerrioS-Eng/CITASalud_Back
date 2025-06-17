package com.project.citasalud.userProfileController;

import com.project.citasalud.userProfile.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", allowedHeaders = "true")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "User profile API for managing user profile")
public class UserProfileController {

    private final UserProfileService userProfileService;


    @Operation(summary = "Edit user profile", description = "Edit the registered user profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful personal data change operation",
            content = @Content(schema = @Schema(implementation = UserProfileDTO.class) ) ),
            @ApiResponse(responseCode = "403", description = "Forbidden, Invalid personal data change operation",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/edit-profile")
    public ResponseEntity<UserProfileDTO> editProfile(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader,
            @Valid @RequestBody UserProfileDTO request) {
        return ResponseEntity.ok(userProfileService.editProfile(authHeader, request));
    }

    @Operation(summary = "Get user profile", description = "Get the registered user profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful personal data retrieval operation",
            content = @Content(schema = @Schema(implementation = UserProfileDTO.class) ) ),
            @ApiResponse(responseCode = "403", description = "Forbidden, Invalid personal data retrieval operation",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return ResponseEntity.ok(userProfileService.getProfile(authHeader));
    }
}

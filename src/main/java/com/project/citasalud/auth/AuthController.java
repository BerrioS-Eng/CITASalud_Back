package com.project.citasalud.auth;

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
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Auth Management", description = "Authentication API for managing user")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Send verification code by email, after 3 attempts a warning notification is sent and the account is locked for 15 minutes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return e-mail of the corresponding registered user to whom the verification code will be sent.",
            content = @Content(schema = @Schema(implementation = EmailResponse.class) ) ),
            @ApiResponse(responseCode = "403", description = "Forbidden, invalid credentials", content = @Content(schema = @Schema()))
    })
    @PostMapping("/login")
    public ResponseEntity<EmailResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "User registration", description = "Register a new user and send verification code by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Send verification code to the previously registered email address.",
            content = @Content(schema = @Schema(implementation = EmailResponse.class) ) ),
            @ApiResponse(responseCode = "403", description = "Forbidden, Generates exceptions if validation rules for data are not met",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/register")
    public ResponseEntity<EmailResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }

    @Operation(summary = "User MFA verification", description = "Verify the code sent by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correct code",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class) ) ),
            @ApiResponse(responseCode = "403", description = "Forbidden, Invalid code", content = @Content(schema = @Schema()))
    })
    @PostMapping("/verifyCode")
    public ResponseEntity<AuthResponse> codeMailMFA(@RequestBody CodeEmailRequest codeEmailRequest) {
        return ResponseEntity.ok(authService.verifyCodeAndGenerateTokens(codeEmailRequest));
    }

}

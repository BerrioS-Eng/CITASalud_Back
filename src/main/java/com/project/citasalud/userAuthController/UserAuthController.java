package com.project.citasalud.userAuthController;

import com.project.citasalud.userAuth.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/userauth")
@CrossOrigin()
@RequiredArgsConstructor
@Tag(name = "User Authentication Management", description = "User authentication API for managing user")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @Operation(summary = "Change email", description = "Change the registered email address." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful email change", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden, Invalid change email operation", content = @Content(schema = @Schema()))
    })
    @PostMapping("/change-email")
    public ResponseEntity<String> editEmail(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader,
            @RequestBody UserAuthEmailRequest request){
        return (userAuthService.modifyEmail(authHeader, request) ? ResponseEntity.ok() : ResponseEntity.badRequest()).build();
    }

    @Operation(summary = "Change password user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful password change", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden, Invalid change password operation", content = @Content(schema = @Schema()))
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader,
                                         @RequestBody UserAuthPasswordRequest request){
        return (userAuthService.modifyPassword(authHeader, request) ? ResponseEntity.ok() : ResponseEntity.badRequest()).build();
    }

    @Operation(summary = "Get user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful user data retrieval", content = @Content(schema = @Schema(implementation = UserAuthResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden, Invalid user data retrieval", content = @Content(schema = @Schema()))
    })
    @GetMapping("/get-user")
    public ResponseEntity<UserAuthResponse> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return ResponseEntity.ok(userAuthService.getUserAuth(authHeader));
    }
}

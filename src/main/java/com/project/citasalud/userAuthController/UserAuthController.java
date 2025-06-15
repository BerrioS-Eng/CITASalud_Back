package com.project.citasalud.userAuthController;

import com.project.citasalud.userAuth.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/userauth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/change-email")
    public ResponseEntity<String> editEmail(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader,
            @RequestBody UserAuthEmailRequest request){
        return (userAuthService.modifyEmail(authHeader, request) ? ResponseEntity.ok() : ResponseEntity.badRequest()).build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader,
                                         @RequestBody UserAuthPasswordRequest request){
        return (userAuthService.modifyPassword(authHeader, request) ? ResponseEntity.ok() : ResponseEntity.badRequest()).build();
    }

    @GetMapping("/get-user")
    public ResponseEntity<UserAuthResponse> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return ResponseEntity.ok(userAuthService.getUserAuth(authHeader));
    }
}

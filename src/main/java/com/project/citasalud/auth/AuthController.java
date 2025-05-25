package com.project.citasalud.auth;

import com.project.citasalud.user.User;
import com.project.citasalud.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    /*
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    /*
    @PostMapping("/encode-passwords")
    public ResponseEntity<String> encodeAllPassword(){
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String rawPassword = user.getPassword();
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        }

        return ResponseEntity.ok("Successfully encoded all passwords");
    }
     */
}

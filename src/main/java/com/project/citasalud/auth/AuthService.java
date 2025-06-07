package com.project.citasalud.auth;

import com.project.citasalud.jwt.JwtService;
import com.project.citasalud.user.Role;
import com.project.citasalud.user.User;
import com.project.citasalud.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getDni(), loginRequest.getPassword()));
        UserDetails user=userRepository.findByDni(loginRequest.getDni()).orElseThrow();
        String token=jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .dni(registerRequest.getDni())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName((registerRequest.getFirstName()))
                .lastName(registerRequest.getLastName())
                .department(registerRequest.getDepartment())
                .city(registerRequest.getCity())
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .numberPhone(registerRequest.getNumberPhone())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }
}

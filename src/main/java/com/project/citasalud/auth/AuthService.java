package com.project.citasalud.auth;

import com.project.citasalud.jwt.JwtService;
import com.project.citasalud.tokenJWT.Token;
import com.project.citasalud.tokenJWT.TokenRepository;
import com.project.citasalud.tokenJWT.TokenType;
import com.project.citasalud.user.Role;
import com.project.citasalud.user.User;
import com.project.citasalud.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getDni(), loginRequest.getPassword()));
        User user = userRepository.findByDni(loginRequest.getDni()).orElseThrow();
        String token = jwtService.getToken(user);
        String refreshToken = jwtService.getRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, token);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
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

        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.getToken(savedUser);
        var refreshToken = jwtService.getRefreshToken(savedUser);
        saveUserToken(savedUser, jwtToken);
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserDetails user) {
        final List<Token> validUserToken = tokenRepository
                .findAllValidIsFalseOrRevokedIsFalseByUser_Dni(user.getUsername());
        if (!validUserToken.isEmpty()) {
            validUserToken.forEach(
                    token -> {
                            token.setRevoked(true);
                            token.setExpired(true);
                    }
            );
            tokenRepository.saveAll(validUserToken);
        }
    }

    public AuthResponse refreshToken(final String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid bearer token");
        }

        String refreshToken = authHeader.substring(7);
        String userDni = jwtService.getDniFromToken(refreshToken);

        if (userDni == null){
            throw new IllegalArgumentException("Invalid refresh token");
        }

        User user = userRepository.findByDni(userDni)
                .orElseThrow(() -> new UsernameNotFoundException(userDni));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String newToken = jwtService.getToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, newToken);
        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .build();
    }
}

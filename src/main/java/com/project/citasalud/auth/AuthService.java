package com.project.citasalud.auth;

import com.project.citasalud.codeMFA.VerificationCodeService;
import com.project.citasalud.eventHandlers.ClientIpHelper;
import com.project.citasalud.eventHandlers.FailedLoginAttemptEvent;
import com.project.citasalud.jwt.JwtService;
import com.project.citasalud.mfa.EmailNotificationService;
import com.project.citasalud.tokenJWT.Token;
import com.project.citasalud.tokenJWT.TokenRepository;
import com.project.citasalud.tokenJWT.TokenType;
import com.project.citasalud.userAuth.Role;
import com.project.citasalud.userAuth.UserAuth;
import com.project.citasalud.userAuth.UserAuthRepository;
import com.project.citasalud.userProfile.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final VerificationCodeService verificationCodeService;
    private final EmailNotificationService emailNotificationService;
    private final ApplicationEventPublisher authenticationEventPublisher;
    private final ClientIpHelper clientIpHelper;

    public EmailResponse login(LoginRequest loginRequest) {
        try {
            Optional<UserAuth> userAuthOpt = userAuthRepository.findByDni(loginRequest.getDni());
            if (userAuthOpt.isPresent()){
                UserAuth userAuth = userAuthOpt.get();

                if (userAuth.isLocked() && userAuth.getUnlockedAt() != null){
                    //Verificar si hay superado el tiempo de bloqueo establecido
                    if (Instant.now().isBefore(userAuth.getUnlockedAt())){
                        throw new RuntimeException("Account temporarily blocked. Please try again later.");
                    } else {
                        userAuth.setLocked(false);
                        userAuth.setUnlockedAt(null);
                        userAuth.setFailedLoginAttempts(0L);
                        userAuthRepository.save(userAuth);

                    }
                }
            }

            verificationCodeService.verificationCodeExpired(userAuthOpt.get().getEmail());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getDni(),
                            loginRequest.getPassword()
                    )
            );

            resetFailedAttempts(loginRequest.getDni());

            Optional<UserAuth> userAuth = userAuthRepository.findByDni(loginRequest.getDni());

            emailNotificationService.sendVerificationCode(userAuth.get().getEmail());

            return EmailResponse.builder()
                    .email(userAuth.get().getEmail())
                    .build();
        } catch (BadCredentialsException e) {
            handleFailedLogin(loginRequest.getDni(), getClientIpAddress());
            throw e;
        }
    }

    public EmailResponse register(RegisterRequest registerRequest) {
        UserProfile userProfile = UserProfile.builder()
                .firstName((registerRequest.getFirstName()))
                .lastName(registerRequest.getLastName())
                .department(registerRequest.getDepartment())
                .city(registerRequest.getCity())
                .address(registerRequest.getAddress())
                .numberPhone(registerRequest.getNumberPhone())
                .build();

        UserAuth userAuth = UserAuth.builder()
                .dni(registerRequest.getDni())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(Role.USER)
                .userProfile(userProfile)
                .locked(false)
                .build();

        UserAuth savedUserAuth = userAuthRepository.save(userAuth);

        emailNotificationService.sendVerificationCode(savedUserAuth.getEmail());

        return EmailResponse.builder()
                .email(userAuth.getEmail())
                .build();
    }

    public void saveUserToken(UserAuth userAuth, String jwtToken) {
        var token = Token.builder()
                .userAuth(userAuth)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserDetails user) {
        final List<Token> validUserToken = tokenRepository
                .findAllValidIsFalseOrRevokedIsFalseByUserAuth_Dni(user.getUsername());
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

        String refreshToken = authHeader.substring(7);

        UserAuth userAuth = getUserValidatedByVerifiedToken(authHeader);

        String newToken = jwtService.getToken(userAuth);
        revokeAllUserTokens(userAuth);
        saveUserToken(userAuth, newToken);
        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse verifyCodeAndGenerateTokens(CodeEmailRequest codeEmailRequest) {
        if (!verificationCodeService.verifyCode(codeEmailRequest.getEmail(), codeEmailRequest.getCode())) {
            throw new IllegalArgumentException("Invalid or expired verification code");
        }

        UserAuth userAuth = userAuthRepository.findByEmail(codeEmailRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(codeEmailRequest.getEmail()));

        String token = jwtService.getToken(userAuth);
        String refreshToken = jwtService.getRefreshToken(userAuth);

        revokeAllUserTokens(userAuth);
        saveUserToken(userAuth, token);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    private void handleFailedLogin(String dni, String clientIpAddress) {
        Optional<UserAuth> userAuthOpt = userAuthRepository.findByDni(dni);

        if (userAuthOpt.isPresent()) {
            UserAuth userAuth = userAuthOpt.get();
            long currentAttemps = userAuth.getFailedLoginAttempts() != null ?
                    userAuth.getFailedLoginAttempts() : 0;

            userAuth.setFailedLoginAttempts(currentAttemps + 1);

            if (userAuth.getFailedLoginAttempts() >= 3) {
                userAuth.setLocked(true);
                userAuth.setUnlockedAt(Instant.now().plusSeconds(900)); // 15 minutos
            }

            userAuthRepository.save(userAuth);

            authenticationEventPublisher.publishEvent(new FailedLoginAttemptEvent(
                            this,
                            userAuth.getEmail(),
                            clientIpAddress,
                            userAuth.getFailedLoginAttempts().intValue(),
                            String.valueOf(Instant.now())
                    )
            );
        }
    }

    private void resetFailedAttempts(String dni) {
        Optional<UserAuth> userAuthOpt = userAuthRepository.findByDni(dni);
        if (userAuthOpt.isPresent()){
            UserAuth userAuth = userAuthOpt.get();
            userAuth.setFailedLoginAttempts(0L);
            userAuth.setLocked(false);
            userAuth.setUnlockedAt(null);
            userAuthRepository.save(userAuth);
        }
    }

    private String getClientIpAddress(){
        return clientIpHelper.getClientIpAddress();
    }

    public UserAuth getUserValidatedByVerifiedToken(String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid bearer token");
        }

        String jwtToken = authHeader.substring(7);
        String userDni = jwtService.getDniFromToken(jwtToken);
        Optional<UserAuth> userAuthOptional = userAuthRepository.findByDni(userDni);

        if (userAuthOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }

        if (!jwtService.isTokenValid(jwtToken, userAuthOptional.get())){
            throw new IllegalArgumentException("Invalid token");
        }

        return userAuthOptional.get();
    }

}

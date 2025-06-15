package com.project.citasalud.userAuth;

import com.project.citasalud.auth.AuthService;
import com.project.citasalud.userAuthController.UserAuthEmailRequest;
import com.project.citasalud.userAuthController.UserAuthPasswordRequest;
import com.project.citasalud.userAuthController.UserAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public Boolean modifyEmail(final String authHeader, UserAuthEmailRequest request){
        UserAuth userAuth = authService.getUserValidatedByVerifiedToken(authHeader);
        userAuth.setEmail(request.getEmail());
        try {
            userAuthRepository.save(userAuth);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public Boolean modifyPassword(final String authHeader, UserAuthPasswordRequest request){
        UserAuth userAuth = authService.getUserValidatedByVerifiedToken(authHeader);

        if (
                passwordEncoder.matches(request.getCurrentPassword(), userAuth.getPassword()) &&
                        request.getNewPassword().equals(request.getMatchPassword())
        ){
            userAuth.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userAuthRepository.save(userAuth);
            return true;
        }
        return false;
    }

    public UserAuthResponse getUserAuth(final String authHeader){
        UserAuth userAuth = authService.getUserValidatedByVerifiedToken(authHeader);
        return UserAuthResponse.builder()
                .dni(userAuth.getDni())
                .email(userAuth.getEmail())
                .build();
    }
}

package com.project.citasalud.userProfile;

import com.project.citasalud.auth.AuthService;
import com.project.citasalud.userAuth.UserAuth;
import com.project.citasalud.userProfileController.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;

    public UserProfileDTO editProfile(final String authHeader, UserProfileDTO request){
        UserAuth userAuth = authService.getUserValidatedByVerifiedToken(authHeader);

        UserProfile profile = userAuth.getUserProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDepartment(request.getDepartment());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setNumberPhone(request.getNumberPhone());

        userProfileRepository.save(profile);

        return UserProfileDTO.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .department(profile.getDepartment())
                .city(profile.getCity())
                .address(profile.getAddress())
                .numberPhone(profile.getNumberPhone())
                .build();
    }

    public UserProfileDTO getProfile(final String authHeader){
        UserAuth userAuth = authService.getUserValidatedByVerifiedToken(authHeader);

        return UserProfileDTO.builder()
                .firstName(userAuth.getUserProfile().getFirstName())
                .lastName(userAuth.getUserProfile().getLastName())
                .department(userAuth.getUserProfile().getDepartment())
                .city(userAuth.getUserProfile().getCity())
                .address(userAuth.getUserProfile().getAddress())
                .numberPhone(userAuth.getUserProfile().getNumberPhone())
                .build();
    }
}

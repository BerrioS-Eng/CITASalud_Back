package com.project.citasalud.userProfile;

import com.project.citasalud.userAuth.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    UserProfile getByUserAuth(UserAuth userAuth);
}

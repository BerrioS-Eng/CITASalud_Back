package com.project.citasalud.userAuth;

import com.project.citasalud.tokenJWT.Token;
import com.project.citasalud.userProfile.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user_auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"tokens", "userProfile"})
@EqualsAndHashCode(exclude = {"tokens", "userProfile"})
public class UserAuth implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, length = 10)
    private String dni;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private Long failedLoginAttempts;
    @Column(nullable = false)
    private boolean locked;
    private Instant unlockedAt;

    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(mappedBy = "userAuth", fetch = FetchType.LAZY)
    private List<Token> tokens;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id", unique = true)
    private UserProfile userProfile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        //return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
        //return UserDetails.super.isEnabled();
        return true;
    }

    // Implementación del método getUsername requerido por UserDetails
    @Override
    public String getUsername() {
        return dni; // Devuelve el campo dni como "username"
    }

}

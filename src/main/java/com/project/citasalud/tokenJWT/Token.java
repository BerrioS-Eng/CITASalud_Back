package com.project.citasalud.tokenJWT;

import com.project.citasalud.userAuth.UserAuth;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userAuth"})
@EqualsAndHashCode(exclude = {"userAuth"})
public class Token implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    private boolean revoked;

    private boolean expired;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userAuth_id")
    private UserAuth userAuth;
}

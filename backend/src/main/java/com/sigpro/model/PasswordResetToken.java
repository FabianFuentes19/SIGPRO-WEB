package com.sigpro.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET_TOKEN")
@Data
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PK")
    private Long id;

    @Column(name = "TOKEN", nullable = false, length = 6)
    private String token;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "USUARIO_ID_FK")
    private Usuario usuario;

    @Column(name = "EXPIRATION_DATE", nullable = false)
    private LocalDateTime expiryDate;

    //verifica si el token ya expiro
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}

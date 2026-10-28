package com.example.demo.infraestructura.adaptadores.salida.bd;
import jakarta.persistence.*;
@Entity @Table(name="password_reset_tokens")
public class PasswordResetTokenEntity {
    @Id public String tokenHash;
    @Column(nullable=false) public Long usuarioId;
    @Column(nullable=false) public java.time.Instant expiresAt;
}

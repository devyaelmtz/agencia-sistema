package com.example.demo.dominio.modelo;
public record PasswordResetToken(String tokenHash, Long usuarioId, java.time.Instant expiresAt) {}

package com.example.demo.dominio.puerto;
import com.example.demo.dominio.modelo.PasswordResetToken;
public interface PasswordResetPort {
    void guardar(PasswordResetToken token);
    java.util.Optional<PasswordResetToken> bloquear(String hash);
    void eliminarPorUsuario(Long usuarioId);
}

package com.example.demo.aplicacion.servicio;
import com.example.demo.dominio.puerto.*;
import com.example.demo.dominio.modelo.PasswordResetToken;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@org.springframework.stereotype.Service
public class PasswordResetService {
    private final UsuarioRepositoryPort users; private final PasswordResetPort tokens; private final ResetEmailPort mail;
    public PasswordResetService(UsuarioRepositoryPort users,PasswordResetPort tokens,ResetEmailPort mail) {this.users=users;this.tokens=tokens;this.mail=mail;}
    public static String hash(String token) {
        try { return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch(java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    @org.springframework.transaction.annotation.Transactional
    public void solicitar(String correo) {
        if(correo==null || correo.length()>100 || !correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("Introduce un correo válido.");
        var u=users.findByCorreo(correo.trim().toLowerCase(java.util.Locale.ROOT));
        if(u==null) return;
        byte[] bytes=new byte[32];new java.security.SecureRandom().nextBytes(bytes);
        String token=java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.eliminarPorUsuario(u.getId());tokens.guardar(new PasswordResetToken(hash(token),u.getId(),Instant.now().plusSeconds(900)));
        mail.enviar(u.getCorreo(),token);
    }
    @org.springframework.transaction.annotation.Transactional
    public void reset(String token,String password) {
        if(token==null || !token.matches("[A-Za-z0-9_-]{43}")) throw new IllegalArgumentException("Código inválido o caducado.");
        if(password==null || password.length()<8 || password.length()>64 || password.getBytes(StandardCharsets.UTF_8).length>72) throw new IllegalArgumentException("Contraseña de 8 a 64 caracteres y máximo 72 bytes.");
        var t=tokens.bloquear(hash(token)).filter(v->v.expiresAt().isAfter(Instant.now())).orElseThrow(()->new IllegalArgumentException("Código inválido o caducado."));
        var u=users.findById(t.usuarioId());if(u==null) throw new IllegalArgumentException("Código inválido o caducado.");
        u.setPassword(new BCryptPasswordEncoder().encode(password)); users.guardar(u); tokens.eliminarPorUsuario(u.getId());
    }
}

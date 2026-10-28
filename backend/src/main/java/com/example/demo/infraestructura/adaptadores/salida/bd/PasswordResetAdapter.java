package com.example.demo.infraestructura.adaptadores.salida.bd;
import com.example.demo.dominio.modelo.PasswordResetToken;
import com.example.demo.dominio.puerto.PasswordResetPort;
@org.springframework.stereotype.Component
public class PasswordResetAdapter implements PasswordResetPort {
    private final SpringDataPasswordResetRepository db;
    public PasswordResetAdapter(SpringDataPasswordResetRepository db) { this.db=db; }
    public void guardar(PasswordResetToken t) { var e=new PasswordResetTokenEntity();e.tokenHash=t.tokenHash();e.usuarioId=t.usuarioId();e.expiresAt=t.expiresAt();db.save(e); }
    public java.util.Optional<PasswordResetToken> bloquear(String hash) { return db.bloquear(hash).map(e->new PasswordResetToken(e.tokenHash,e.usuarioId,e.expiresAt)); }
    public void eliminarPorUsuario(Long id) { db.deleteByUsuarioId(id); }
}

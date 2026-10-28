package com.example.demo.infraestructura.adaptadores.salida.bd;
import org.springframework.data.jpa.repository.*;
public interface SpringDataPasswordResetRepository extends JpaRepository<PasswordResetTokenEntity,String> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from PasswordResetTokenEntity t where t.tokenHash = :hash")
    java.util.Optional<PasswordResetTokenEntity> bloquear(@org.springframework.data.repository.query.Param("hash") String hash);
    void deleteByUsuarioId(Long usuarioId);
}

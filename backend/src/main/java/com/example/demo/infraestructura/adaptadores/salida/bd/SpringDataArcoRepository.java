package com.example.demo.infraestructura.adaptadores.salida.bd;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

public interface SpringDataArcoRepository extends JpaRepository<ArcoRequestEntity, Long> {
  java.util.List<ArcoRequestEntity> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

  java.util.List<ArcoRequestEntity> findAllByOrderByCreatedAtDesc();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from ArcoRequestEntity a where a.id=:id")
  java.util.Optional<ArcoRequestEntity> bloquear(Long id);
}

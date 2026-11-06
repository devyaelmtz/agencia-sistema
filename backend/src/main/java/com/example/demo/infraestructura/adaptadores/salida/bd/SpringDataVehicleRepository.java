package com.example.demo.infraestructura.adaptadores.salida.bd;

import org.springframework.data.jpa.repository.*;

public interface SpringDataVehicleRepository extends JpaRepository<VehicleEntity, Long> {
  @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @Query("select v from VehicleEntity v where v.id=:id")
  java.util.Optional<VehicleEntity> bloquear(Long id);
}

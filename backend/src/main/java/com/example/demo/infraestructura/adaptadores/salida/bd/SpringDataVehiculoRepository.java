package com.example.demo.infraestructura.adaptadores.salida.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataVehiculoRepository extends JpaRepository<VehiculoEntity, Long> {
    List<VehiculoEntity> findByColorIgnoreCase(String color);
}
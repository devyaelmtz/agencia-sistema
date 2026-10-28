package com.example.demo.infraestructura.adaptadores.salida.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataSucursalRepository extends JpaRepository<SucursalEntity, Long> {
}
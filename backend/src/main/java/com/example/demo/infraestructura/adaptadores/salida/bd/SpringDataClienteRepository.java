package com.example.demo.infraestructura.adaptadores.salida.bd;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, Long> {
    ClienteEntity findByCorreoAndTelefono(String correo, String telefono);
}
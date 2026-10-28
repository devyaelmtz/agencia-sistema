package com.example.demo.infraestructura.adaptadores.salida.bd;
public interface SpringDataArcoRepository extends org.springframework.data.jpa.repository.JpaRepository<ArcoRequestEntity,Long> { java.util.List<ArcoRequestEntity> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId); }

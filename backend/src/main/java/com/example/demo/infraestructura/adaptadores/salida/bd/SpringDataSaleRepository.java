package com.example.demo.infraestructura.adaptadores.salida.bd;

public interface SpringDataSaleRepository
    extends org.springframework.data.jpa.repository.JpaRepository<SaleEntity, Long> {
  boolean existsByVehicleId(Long id);
}

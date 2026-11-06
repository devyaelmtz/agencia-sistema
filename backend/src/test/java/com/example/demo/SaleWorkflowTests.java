package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.aplicacion.servicio.*;
import com.example.demo.dominio.modelo.Vehicle;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
@WithMockUser(roles = "ADMIN")
class SaleWorkflowTests {
  @Autowired SaleService sales;
  @Autowired VehicleService vehicles;
  @Autowired ClientService clients;
  @Autowired AuthUseCase auth;

  @Test
  void saleIsAtomicPreventsDoubleSaleAndDeletionRestoresAvailability() {
    var actor =
        auth.registrar("Vendedor", "sale-actor@example.invalid", "5551234567", "ClaveSegura123");
    var c =
        clients.crear(
            actor.getId(),
            new ClientService.Datos(
                "Cliente",
                "Prueba",
                "XAXX010101000",
                "Calle Principal 123",
                "5551234567",
                "sale-client@example.invalid",
                false));
    var v =
        vehicles.guardar(
            null,
            new Vehicle(
                null,
                "Toyota",
                "Corolla",
                2024,
                "1HGCM82633A004359",
                new BigDecimal("200000.00"),
                10L,
                Vehicle.Estado.RESERVADO));
    var data =
        new SaleService.Datos(
            c.id(), v.id(), new BigDecimal("200000.00"), SaleEntity.MetodoPago.TRANSFERENCIA);
    var sale = sales.crear(actor.getId(), data);
    assertEquals(actor.getId(), sale.usuarioId());
    assertEquals(Vehicle.Estado.VENDIDO, vehicles.obtener(v.id()).estado());
    assertEquals(
        409,
        assertThrows(ResponseStatusException.class, () -> sales.crear(actor.getId(), data))
            .getStatusCode()
            .value());
    assertThrows(ResponseStatusException.class, () -> vehicles.guardar(v.id(), v));
    var updated =
        sales.actualizar(
            sale.id(),
            sale.version(),
            new SaleService.Datos(
                c.id(), v.id(), new BigDecimal("199000.00"), SaleEntity.MetodoPago.EFECTIVO));
    assertEquals(new BigDecimal("199000.00"), updated.montoTotal());
    assertThrows(ResponseStatusException.class, () -> sales.eliminar(sale.id(), sale.version()));
    sales.eliminar(sale.id(), updated.version());
    assertEquals(Vehicle.Estado.DISPONIBLE, vehicles.obtener(v.id()).estado());
  }
}

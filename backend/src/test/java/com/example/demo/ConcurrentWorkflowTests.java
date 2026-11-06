package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.aplicacion.servicio.*;
import com.example.demo.dominio.modelo.*;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
class ConcurrentWorkflowTests {
  @Autowired AuthUseCase auth;
  @Autowired ClientService clients;
  @Autowired VehicleService vehicles;
  @Autowired ApprovalService approvals;
  @Autowired SaleService sales;
  @Autowired SpringDataUsuarioRepository users;
  @Autowired SpringDataClienteRepository clientDb;
  @Autowired SpringDataVehicleRepository vehicleDb;
  @Autowired SpringDataSaleRepository saleDb;
  @Autowired SpringDataArcoRepository requestDb;

  List<Integer> race(Callable<?> action) throws Exception {
    var start = new CountDownLatch(1);
    try (var executor = Executors.newFixedThreadPool(2)) {
      Callable<Integer> task =
          () -> {
            start.await();
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                    "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
            SecurityContextHolder.setContext(context);
            try {
              action.call();
              return 200;
            } catch (ResponseStatusException e) {
              return e.getStatusCode().value();
            } finally {
              SecurityContextHolder.clearContext();
            }
          };
      var one = executor.submit(task);
      var two = executor.submit(task);
      start.countDown();
      return List.of(one.get(20, TimeUnit.SECONDS), two.get(20, TimeUnit.SECONDS));
    }
  }

  @Test
  void concurrentApprovalsAndSalesHaveOneWinner() throws Exception {
    String tag = UUID.randomUUID().toString();
    var owner =
        auth.registrar(
            "Concurrent owner",
            "race-o-" + tag + "@example.invalid",
            "5551234567",
            "ClaveSegura123");
    var admin =
        auth.registrar(
            "Concurrent admin",
            "race-a-" + tag + "@example.invalid",
            "5551234567",
            "ClaveSegura123");
    var c =
        clients.crear(
            owner.getId(),
            new ClientService.Datos(
                "Cliente",
                "Concurrente",
                "XAXX010101000",
                "Calle Prueba 123",
                "5551234567",
                "race-c@example.invalid",
                true));
    var v =
        vehicles.guardar(
            null,
            new Vehicle(
                null,
                "Toyota",
                "Corolla",
                2024,
                "1HGCM82633A004358",
                new BigDecimal("200000.00"),
                100L,
                Vehicle.Estado.DISPONIBLE));
    var request =
        approvals.solicitar(
            owner.getId(),
            "clientes",
            c.id(),
            ArcoRequest.Tipo.OPOSICION,
            c.version(),
            null,
            "Solicito no recibir publicidad",
            true,
            ArcoService.POLICY_VERSION);
    try {
      var results =
          race(() -> approvals.resolver(request.id, admin.getId(), true, "Solicitud verificada"));
      assertEquals(1, results.stream().filter(s -> s == 200).count());
      assertEquals(1, results.stream().filter(s -> s == 409).count());
      assertFalse(clients.entity(c.id()).getRecibirPublicidad());
      var data =
          new SaleService.Datos(
              c.id(), v.id(), new BigDecimal("200000.00"), SaleEntity.MetodoPago.TRANSFERENCIA);
      results = race(() -> sales.crear(admin.getId(), data));
      assertEquals(1, results.stream().filter(s -> s == 200).count());
      assertEquals(1, results.stream().filter(s -> s == 409).count());
      assertEquals(1, saleDb.findAll().stream().filter(s -> s.vehicle.id.equals(v.id())).count());
      assertEquals(Vehicle.Estado.VENDIDO, vehicles.obtener(v.id()).estado());
    } finally {
      saleDb.findAll().stream().filter(s -> s.vehicle.id.equals(v.id())).forEach(saleDb::delete);
      requestDb.deleteById(request.id);
      vehicleDb.deleteById(v.id());
      clientDb.deleteById(c.id());
      users.deleteById(owner.getId());
      users.deleteById(admin.getId());
    }
  }
}

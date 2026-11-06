package com.example.demo.aplicacion.servicio;

import static com.example.demo.aplicacion.servicio.ApiValidation.*;

import com.example.demo.dominio.modelo.Vehicle.Estado;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class SaleService {
  public record Datos(
      Long clienteId, Long vehiculoId, BigDecimal montoTotal, SaleEntity.MetodoPago metodoPago) {}

  public record Vista(
      Long id,
      Long version,
      Long clienteId,
      Long vehiculoId,
      Long usuarioId,
      Instant fecha,
      BigDecimal montoTotal,
      SaleEntity.MetodoPago metodoPago) {}

  private final SpringDataSaleRepository db;
  private final SpringDataClienteRepository clients;
  private final SpringDataVehicleRepository vehicles;
  private final SpringDataUsuarioRepository users;

  public SaleService(
      SpringDataSaleRepository db,
      SpringDataClienteRepository clients,
      SpringDataVehicleRepository vehicles,
      SpringDataUsuarioRepository users) {
    this.db = db;
    this.clients = clients;
    this.vehicles = vehicles;
    this.users = users;
  }

  private SaleEntity entity(Long id) {
    return db.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  private Vista view(SaleEntity e) {
    return new Vista(
        e.id,
        e.version,
        e.cliente.getId(),
        e.vehicle.id,
        e.usuario.getId(),
        e.fecha,
        e.montoTotal,
        e.metodoPago);
  }

  public java.util.List<Vista> listar() {
    return db.findAll().stream().map(this::view).toList();
  }

  public Vista obtener(Long id) {
    return view(entity(id));
  }

  private void validate(Datos d) {
    require(d != null, "Datos de venta obligatorios");
    require(
        d.clienteId() != null && d.vehiculoId() != null && d.metodoPago() != null,
        "Cliente, vehículo y método son obligatorios");
    require(
        d.montoTotal() != null
            && d.montoTotal().signum() > 0
            && d.montoTotal().scale() <= 2
            && d.montoTotal().precision() - d.montoTotal().scale() <= 13,
        "Monto positivo, máximo 13 enteros y 2 decimales");
  }

  public Vista crear(Long actor, Datos d) {
    validate(d);
    var v =
        vehicles
            .bloquear(d.vehiculoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if ((v.estado != Estado.DISPONIBLE && v.estado != Estado.RESERVADO)
        || db.existsByVehicleId(v.id))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehículo no disponible");
    var e = new SaleEntity();
    e.cliente =
        clients
            .findById(d.clienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    e.usuario =
        users
            .findById(actor)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    e.vehicle = v;
    e.fecha = Instant.now();
    e.montoTotal = d.montoTotal();
    e.metodoPago = d.metodoPago();
    v.estado = Estado.VENDIDO;
    vehicles.saveAndFlush(v);
    return view(db.saveAndFlush(e));
  }

  public Vista actualizar(Long id, Long expected, Datos d) {
    validate(d);
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    require(
        e.vehicle.id.equals(d.vehiculoId()),
        "El vehículo no se cambia en una venta registrada; elimina y registra otra venta");
    e.cliente =
        clients
            .findById(d.clienteId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    e.montoTotal = d.montoTotal();
    e.metodoPago = d.metodoPago();
    return view(db.saveAndFlush(e));
  }

  public void eliminar(Long id, Long expected) {
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    var v = vehicles.bloquear(e.vehicle.id).orElseThrow();
    db.delete(e);
    db.flush();
    v.estado = Estado.DISPONIBLE;
    vehicles.saveAndFlush(v);
  }
}

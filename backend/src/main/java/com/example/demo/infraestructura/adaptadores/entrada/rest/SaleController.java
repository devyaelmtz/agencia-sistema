package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.SaleService;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class SaleController {
  private final SaleService service;

  public SaleController(SaleService service) {
    this.service = service;
  }

  public record Change(Long version, SaleService.Datos datos) {}

  @GetMapping
  public java.util.List<SaleService.Vista> list() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public SaleService.Vista get(@PathVariable Long id) {
    return service.obtener(id);
  }

  @PostMapping
  public ResponseEntity<SaleService.Vista> create(
      @AuthenticationPrincipal Jwt jwt, @RequestBody SaleService.Datos d) {
    var v = service.crear(Long.valueOf(jwt.getSubject()), d);
    return ResponseEntity.created(java.net.URI.create("/api/ventas/" + v.id())).body(v);
  }

  @PutMapping("/{id}")
  public SaleService.Vista update(@PathVariable Long id, @RequestBody Change c) {
    return service.actualizar(id, c.version(), c.datos());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id, @RequestParam Long version) {
    service.eliminar(id, version);
  }
}

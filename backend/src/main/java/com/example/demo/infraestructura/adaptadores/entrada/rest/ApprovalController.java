package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.ApprovalService;
import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import com.example.demo.infraestructura.adaptadores.salida.bd.ArcoRequestEntity;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

@RestController
public class ApprovalController {
  private final ApprovalService service;
  private final JsonMapper json;
  private final com.example.demo.aplicacion.servicio.ClientService clients;

  public ApprovalController(
      ApprovalService service,
      JsonMapper json,
      com.example.demo.aplicacion.servicio.ClientService clients) {
    this.service = service;
    this.json = json;
    this.clients = clients;
  }

  public record Request(
      String tablaAfectada,
      Long registroId,
      Long registroVersion,
      Tipo tipo,
      java.util.Map<String, Object> propuesta,
      String descripcion,
      boolean avisoLeido,
      String avisoVersion) {}

  public record Decision(String decision, String motivo) {}

  @GetMapping("/api/arco/my-requests")
  public java.util.List<ArcoRequestEntity> own(@AuthenticationPrincipal Jwt jwt) {
    return service.propias(Long.valueOf(jwt.getSubject()));
  }

  @PostMapping("/api/arco-requests/actions")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ArcoRequestEntity request(@AuthenticationPrincipal Jwt jwt, @RequestBody Request r) {
    return service.solicitar(
        Long.valueOf(jwt.getSubject()),
        r.tablaAfectada(),
        r.registroId(),
        r.tipo(),
        r.registroVersion(),
        r.propuesta() == null ? null : json.writeValueAsString(r.propuesta()),
        r.descripcion(),
        r.avisoLeido(),
        r.avisoVersion());
  }

  @GetMapping("/api/admin/arco-requests")
  public java.util.List<ArcoRequestEntity> all() {
    return service.todas();
  }

  @PostMapping("/api/admin/arco-requests/{id}/resolve")
  public ArcoRequestEntity decide(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @RequestBody Decision d) {
    com.example.demo.aplicacion.servicio.ApiValidation.require(
        d.decision() != null && java.util.Set.of("APROBAR", "RECHAZAR").contains(d.decision()),
        "Decisión inválida");
    return service.resolver(
        id, Long.valueOf(jwt.getSubject()), d.decision().equals("APROBAR"), d.motivo());
  }

  public record Edit(Long version, String descripcion) {}

  @GetMapping("/api/arco-requests/{id}")
  public ArcoRequestEntity detail(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      org.springframework.security.core.Authentication auth) {
    return service.obtener(
        id,
        Long.valueOf(jwt.getSubject()),
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
  }

  @PutMapping("/api/arco-requests/{id}")
  public ArcoRequestEntity edit(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @RequestBody Edit edit) {
    return service.editar(id, Long.valueOf(jwt.getSubject()), edit.version(), edit.descripcion());
  }

  @DeleteMapping("/api/arco-requests/{id}")
  public ArcoRequestEntity withdraw(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @RequestParam Long version) {
    return service.retirar(id, Long.valueOf(jwt.getSubject()), version);
  }

  public record Owner(Long version, Long usuarioId) {}

  @PutMapping("/api/admin/clientes/{id}/owner")
  public com.example.demo.aplicacion.servicio.ClientService.Vista link(
      @PathVariable Long id, @RequestBody Owner data) {
    return clients.vincular(id, data.version(), data.usuarioId());
  }
}

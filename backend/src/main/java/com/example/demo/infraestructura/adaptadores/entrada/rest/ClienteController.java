package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.*;
import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
  private final ClientService service;
  private final ApprovalService approvals;
  private final JsonMapper json;

  public ClienteController(ClientService service, ApprovalService approvals, JsonMapper json) {
    this.service = service;
    this.approvals = approvals;
    this.json = json;
  }

  public record Change(
      Long version,
      ClientService.Datos datos,
      String descripcion,
      boolean avisoLeido,
      String avisoVersion) {}

  private boolean admin(Authentication auth) {
    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }

  @GetMapping
  public java.util.List<ClientService.Vista> list(
      @AuthenticationPrincipal Jwt jwt, Authentication auth) {
    return service.listar(Long.valueOf(jwt.getSubject()), admin(auth));
  }

  @GetMapping("/{id}")
  public ClientService.Vista get(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt, Authentication auth) {
    return service.obtener(id, Long.valueOf(jwt.getSubject()), admin(auth));
  }

  @PostMapping
  public ResponseEntity<ClientService.Vista> create(
      @AuthenticationPrincipal Jwt jwt, @RequestBody ClientService.Datos data) {
    var v = service.crear(Long.valueOf(jwt.getSubject()), data);
    return ResponseEntity.created(java.net.URI.create("/api/clientes/" + v.id())).body(v);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      Authentication auth,
      @RequestBody Change c) {
    if (admin(auth)) return ResponseEntity.ok(service.actualizar(id, c.version(), c.datos()));
    return ResponseEntity.accepted()
        .body(
            approvals.solicitar(
                Long.valueOf(jwt.getSubject()),
                "clientes",
                id,
                Tipo.RECTIFICACION,
                c.version(),
                json.writeValueAsString(c.datos()),
                c.descripcion(),
                c.avisoLeido(),
                c.avisoVersion()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      Authentication auth,
      @RequestParam Long version,
      @RequestParam(defaultValue = "") String descripcion,
      @RequestParam(defaultValue = "false") boolean avisoLeido,
      @RequestParam(defaultValue = "") String avisoVersion) {
    if (admin(auth)) {
      service.eliminar(id, version);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.accepted()
        .body(
            approvals.solicitar(
                Long.valueOf(jwt.getSubject()),
                "clientes",
                id,
                Tipo.CANCELACION,
                version,
                null,
                descripcion,
                avisoLeido,
                avisoVersion));
  }
}

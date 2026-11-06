package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.*;
import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
  private final UserService service;
  private final ApprovalService approvals;
  private final JsonMapper json;

  public UsuarioController(UserService service, ApprovalService approvals, JsonMapper json) {
    this.service = service;
    this.approvals = approvals;
    this.json = json;
  }

  public record Change(
      Long version,
      UserService.Datos datos,
      String descripcion,
      boolean avisoLeido,
      String avisoVersion) {}

  private boolean admin(Authentication auth) {
    return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public java.util.List<UserService.Vista> list() {
    return service.listar();
  }

  @GetMapping("/{id}")
  public UserService.Vista get(
      @PathVariable Long id, @AuthenticationPrincipal Jwt jwt, Authentication auth) {
    return service.obtener(id, Long.valueOf(jwt.getSubject()), admin(auth));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserService.Vista> create(@RequestBody UserService.Datos data) {
    var v = service.crear(data);
    return ResponseEntity.created(java.net.URI.create("/api/usuarios/" + v.id())).body(v);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable Long id,
      @AuthenticationPrincipal Jwt jwt,
      Authentication auth,
      @RequestBody Change c) {
    if (admin(auth)) return ResponseEntity.ok(service.actualizar(id, c.version(), c.datos()));
    var d = c.datos();
    ApiValidation.require(d != null, "Datos de perfil obligatorios");
    ApiValidation.require(
        d.password() == null && d.rol() == null,
        "Usa recuperación de contraseña; no puedes proponer cambios de rol");
    return ResponseEntity.accepted()
        .body(
            approvals.solicitar(
                Long.valueOf(jwt.getSubject()),
                "usuarios",
                id,
                Tipo.RECTIFICACION,
                c.version(),
                json.writeValueAsString(
                    new UserService.Perfil(d.nombreUsuario(), d.correo(), d.telefono())),
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
    Long actor = Long.valueOf(jwt.getSubject());
    if (admin(auth)) {
      service.eliminar(id, version, actor);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.accepted()
        .body(
            approvals.solicitar(
                actor,
                "usuarios",
                id,
                Tipo.CANCELACION,
                version,
                null,
                descripcion,
                avisoLeido,
                avisoVersion));
  }
}

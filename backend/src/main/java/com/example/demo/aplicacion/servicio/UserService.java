package com.example.demo.aplicacion.servicio;

import static com.example.demo.aplicacion.servicio.ApiValidation.*;

import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

@Service
@Transactional
public class UserService implements ArcoTarget {
  public record Perfil(String nombreUsuario, String correo, String telefono) {}

  public record Datos(
      String nombreUsuario, String correo, String telefono, String password, String rol) {}

  public record Vista(
      Long id, Long version, String nombreUsuario, String correo, String telefono, String rol) {}

  private final SpringDataUsuarioRepository db;
  private final JsonMapper json;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public UserService(SpringDataUsuarioRepository db, JsonMapper json) {
    this.db = db;
    this.json = json;
  }

  public String tabla() {
    return "usuarios";
  }

  public UsuarioEntity entity(Long id) {
    return db.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
  }

  public Vista vista(UsuarioEntity u) {
    return new Vista(
        u.getId(), u.version, u.getNombreUsuario(), u.getCorreo(), u.getTelefono(), u.getRol());
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public java.util.List<Vista> listar() {
    return db.findAll().stream().map(this::vista).toList();
  }

  public Vista obtener(Long id, Long user, boolean admin) {
    version(id, user, admin);
    return vista(entity(id));
  }

  public long version(Long id, Long user, boolean admin) {
    var e = entity(id);
    if (!admin && !id.equals(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    return e.version;
  }

  private Perfil valid(Perfil p) {
    return new Perfil(
        text(p.nombreUsuario(), 2, 50, "nombre"), email(p.correo()), phone(p.telefono()));
  }

  private void apply(UsuarioEntity e, Perfil p) {
    e.setNombreUsuario(p.nombreUsuario());
    e.setCorreo(p.correo());
    e.setTelefono(p.telefono());
  }

  private void password(UsuarioEntity e, String p) {
    require(
        p != null
            && p.length() >= 8
            && p.length() <= 64
            && p.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 72,
        "Contraseña: 8–64 caracteres y máximo 72 bytes");
    e.setPassword(encoder.encode(p));
  }

  private void role(UsuarioEntity e, String r) {
    require(r != null && java.util.Set.of("ROLE_ADMIN", "ROLE_USER").contains(r), "Rol inválido");
    e.setRol(r);
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public Vista crear(Datos d) {
    require(d != null, "Datos de usuario obligatorios");
    var e = new UsuarioEntity();
    apply(e, valid(new Perfil(d.nombreUsuario(), d.correo(), d.telefono())));
    password(e, d.password());
    role(e, d.rol());
    return vista(db.saveAndFlush(e));
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public Vista actualizar(Long id, Long expected, Datos d) {
    require(d != null, "Datos de usuario obligatorios");
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    apply(e, valid(new Perfil(d.nombreUsuario(), d.correo(), d.telefono())));
    if (d.rol() != null) role(e, d.rol());
    if (d.password() != null && !d.password().isBlank()) password(e, d.password());
    return vista(db.saveAndFlush(e));
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public void eliminar(Long id, Long expected, Long actor) {
    require(!id.equals(actor), "No puedes eliminar tu propia cuenta desde administración");
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    db.delete(e);
    db.flush();
  }

  public String preparar(Long id, Tipo action, String proposal) {
    require(action != Tipo.OPOSICION, "La oposición a publicidad se solicita sobre clientes");
    if (action == Tipo.RECTIFICACION) {
      try {
        ApiValidation.require(
            java.util.Set.of("nombreUsuario", "correo", "telefono")
                .containsAll(json.readValue(proposal, java.util.Map.class).keySet()),
            "Campos no permitidos");
        return json.writeValueAsString(valid(json.readValue(proposal, Perfil.class)));
      } catch (Exception e) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Propuesta de perfil inválida; no se aceptan roles ni contraseñas");
      }
    }
    return null;
  }

  public String ejecutar(Long id, Tipo action, String proposal, long expected) {
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    switch (action) {
      case RECTIFICACION -> {
        apply(e, valid(json.readValue(proposal, Perfil.class)));
        db.saveAndFlush(e);
      }
      case CANCELACION -> {
        db.delete(e);
        db.flush();
      }
      case ACCESO -> {
        return json.writeValueAsString(vista(e));
      }
      default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    return null;
  }
}

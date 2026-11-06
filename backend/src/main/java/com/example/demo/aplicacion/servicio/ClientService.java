package com.example.demo.aplicacion.servicio;

import static com.example.demo.aplicacion.servicio.ApiValidation.*;

import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

@Service
@Transactional
public class ClientService implements ArcoTarget {
  public record Datos(
      String nombre,
      String apellido,
      String rfc,
      String direccion,
      String telefono,
      String correo,
      Boolean recibirPublicidad) {}

  public record Vista(
      Long id,
      Long version,
      Long usuarioId,
      String nombre,
      String apellido,
      String rfc,
      String direccion,
      String telefono,
      String correo,
      Boolean recibirPublicidad) {}

  private final SpringDataClienteRepository db;
  private final JsonMapper json;
  private final SpringDataUsuarioRepository users;

  public ClientService(
      SpringDataClienteRepository db, JsonMapper json, SpringDataUsuarioRepository users) {
    this.db = db;
    this.json = json;
    this.users = users;
  }

  public String tabla() {
    return "clientes";
  }

  public ClienteEntity entity(Long id) {
    return db.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
  }

  public Vista vista(ClienteEntity e) {
    return new Vista(
        e.getId(),
        e.version,
        e.usuarioId,
        e.getNombre(),
        e.getApellido(),
        e.rfc,
        e.getDireccion(),
        e.getTelefono(),
        e.getCorreo(),
        e.getRecibirPublicidad());
  }

  public long version(Long id, Long requester, boolean admin) {
    var e = entity(id);
    if (!admin && !requester.equals(e.usuarioId))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    return e.version;
  }

  public java.util.List<Vista> listar(Long user, boolean admin) {
    return db.findAll().stream()
        .filter(e -> admin || user.equals(e.usuarioId))
        .map(this::vista)
        .toList();
  }

  public Vista obtener(Long id, Long user, boolean admin) {
    version(id, user, admin);
    return vista(entity(id));
  }

  private Datos valid(Datos d) {
    require(d != null, "Datos de cliente obligatorios");
    String rfc = text(d.rfc(), 12, 13, "RFC").toUpperCase(java.util.Locale.ROOT);
    require(rfc.matches("[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}"), "RFC inválido");
    return new Datos(
        text(d.nombre(), 2, 100, "nombre"),
        text(d.apellido(), 2, 100, "apellido"),
        rfc,
        text(d.direccion(), 5, 255, "dirección"),
        phone(d.telefono()),
        email(d.correo()),
        Boolean.TRUE.equals(d.recibirPublicidad()));
  }

  private void apply(ClienteEntity e, Datos d) {
    e.setNombre(d.nombre());
    e.setApellido(d.apellido());
    e.rfc = d.rfc();
    e.setDireccion(d.direccion());
    e.setTelefono(d.telefono());
    e.setCorreo(d.correo());
    e.setRecibirPublicidad(d.recibirPublicidad());
  }

  public Vista crear(Long owner, Datos d) {
    var e = new ClienteEntity();
    e.usuarioId = owner;
    apply(e, valid(d));
    return vista(db.saveAndFlush(e));
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public Vista actualizar(Long id, Long expected, Datos d) {
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    apply(e, valid(d));
    return vista(db.saveAndFlush(e));
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public void eliminar(Long id, Long expected) {
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    db.delete(e);
    db.flush();
  }

  public String preparar(Long id, Tipo action, String proposal) {
    if (action == Tipo.RECTIFICACION) {
      try {
        ApiValidation.require(
            java.util.Set.of(
                    "nombre",
                    "apellido",
                    "rfc",
                    "direccion",
                    "telefono",
                    "correo",
                    "recibirPublicidad")
                .containsAll(json.readValue(proposal, java.util.Map.class).keySet()),
            "Campos no permitidos");
        return json.writeValueAsString(valid(json.readValue(proposal, Datos.class)));
      } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Propuesta de cliente inválida");
      }
    }
    return null;
  }

  public String ejecutar(Long id, Tipo action, String proposal, long expected) {
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    switch (action) {
      case RECTIFICACION -> {
        apply(e, valid(json.readValue(proposal, Datos.class)));
        db.saveAndFlush(e);
      }
      case CANCELACION -> {
        db.delete(e);
        db.flush();
        return null;
      }
      case OPOSICION -> {
        e.setRecibirPublicidad(false);
        db.saveAndFlush(e);
      }
      case ACCESO -> {
        return json.writeValueAsString(vista(e));
      }
    }
    return null;
  }

  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public Vista vincular(Long id, Long expected, Long owner) {
    ApiValidation.require(owner != null, "Selecciona el propietario");
    if (!users.existsById(owner))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    var e = entity(id);
    ApiValidation.version(e.version, expected);
    e.usuarioId = owner;
    return vista(db.saveAndFlush(e));
  }
}

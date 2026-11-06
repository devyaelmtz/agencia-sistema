package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ApprovalService {
  private final SpringDataArcoRepository requests;
  private final Map<String, ArcoTarget> targets;

  public ApprovalService(SpringDataArcoRepository requests, List<ArcoTarget> handlers) {
    this.requests = requests;
    targets = new HashMap<>();
    handlers.forEach(h -> targets.put(h.tabla(), h));
  }

  private ArcoTarget target(String table) {
    var h = targets.get(table);
    if (h == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de registro no permitido");
    return h;
  }

  @Transactional
  public ArcoRequestEntity solicitar(
      Long user,
      String table,
      Long id,
      Tipo action,
      Long expected,
      String proposal,
      String description,
      boolean notice,
      String policy) {
    ApiValidation.require(
        action != null && id != null && table != null, "Selecciona el derecho y registro");
    ApiValidation.require(
        notice && ArcoService.POLICY_VERSION.equals(policy), "Confirma el aviso vigente");
    String desc = ApiValidation.text(description, 10, 4000, "descripción");
    var handler = target(table);
    long current = handler.version(id, user, false); // ownership before storing any proposal
    ApiValidation.version(current, expected);
    String normalized = handler.preparar(id, action, proposal);
    var a = new ArcoRequestEntity();
    a.usuarioId = user;
    a.tipo = action;
    a.tablaAfectada = table;
    a.registroId = id;
    a.registroVersion = current;
    a.propuesta = normalized;
    a.descripcion = desc;
    a.estado = "PENDIENTE";
    a.avisoLeido = true;
    a.avisoVersion = policy;
    a.createdAt = Instant.now();
    return requests.saveAndFlush(a);
  }

  @Transactional(readOnly = true)
  public List<ArcoRequestEntity> propias(Long user) {
    return requests.findByUsuarioIdOrderByCreatedAtDesc(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true)
  public List<ArcoRequestEntity> todas() {
    return requests.findAllByOrderByCreatedAtDesc();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ArcoRequestEntity resolver(Long id, Long admin, boolean approve, String reason) {
    String motive = ApiValidation.text(reason, 5, 1000, "motivo de resolución");
    var a =
        requests.bloquear(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!"PENDIENTE".equals(a.estado))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya fue resuelta");
    if (a.usuarioId.equals(admin))
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Otro administrador debe resolver tu solicitud");
    if (approve) {
      if (a.tablaAfectada == null)
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Solicitud histórica sin operación: requiere una propuesta vinculada a un registro");
      var handler = target(a.tablaAfectada);
      ApiValidation.version(handler.version(a.registroId, a.usuarioId, false), a.registroVersion);
      a.resultado = handler.ejecutar(a.registroId, a.tipo, a.propuesta, a.registroVersion);
    }
    a.estado = approve ? "APROBADA" : "RECHAZADA";
    a.administradorId = admin;
    a.fechaResolucion = Instant.now();
    a.motivoResolucion = motive;
    return requests.saveAndFlush(a);
  }

  @Transactional(readOnly = true)
  public ArcoRequestEntity obtener(Long id, Long user, boolean admin) {
    var a =
        requests.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!admin && !a.usuarioId.equals(user))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    return a;
  }

  @Transactional
  public ArcoRequestEntity editar(Long id, Long user, Long expected, String description) {
    var a =
        requests.bloquear(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!a.usuarioId.equals(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    ApiValidation.version(a.version, expected);
    if (!"PENDIENTE".equals(a.estado))
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Solo puedes editar solicitudes pendientes");
    a.descripcion = ApiValidation.text(description, 10, 4000, "descripción");
    return requests.saveAndFlush(a);
  }

  @Transactional
  public ArcoRequestEntity retirar(Long id, Long user, Long expected) {
    var a =
        requests.bloquear(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!a.usuarioId.equals(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    ApiValidation.version(a.version, expected);
    if (!"PENDIENTE".equals(a.estado))
      throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya fue resuelta");
    a.estado = "RECHAZADA";
    a.motivoResolucion = "Retirada por el solicitante";
    a.fechaResolucion = Instant.now();
    return requests.saveAndFlush(a);
  }
}

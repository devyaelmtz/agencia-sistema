package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.aplicacion.servicio.*;
import com.example.demo.dominio.modelo.ArcoRequest.Tipo;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@Transactional
@WithMockUser(roles = "ADMIN")
class ApprovalWorkflowTests {
  @Autowired AuthUseCase auth;
  @Autowired ClientService clients;
  @Autowired UserService users;
  @Autowired ApprovalService approvals;
  @Autowired SpringDataArcoRepository requests;
  @Autowired SpringDataUsuarioRepository accounts;
  @Autowired JsonMapper json;
  Long owner, admin;
  ClientService.Vista client;

  ClientService.Datos data(String name) {
    return new ClientService.Datos(
        name,
        "Prueba",
        "XAXX010101000",
        "Calle Prueba 123",
        "5551234567",
        "cliente@example.invalid",
        true);
  }

  @BeforeEach
  void setup() {
    owner =
        auth.registrar(
                "Solicitante", "approval-owner@example.invalid", "5551234567", "ClaveSegura123")
            .getId();
    admin =
        auth.registrar(
                "Administrador", "approval-admin@example.invalid", "5551234567", "ClaveSegura123")
            .getId();
    client = clients.crear(owner, data("Original"));
  }

  ArcoRequestEntity request(Tipo type) {
    return approvals.solicitar(
        owner,
        "clientes",
        client.id(),
        type,
        client.version(),
        json.writeValueAsString(data("Actualizado")),
        "Solicito el cambio de mis datos",
        true,
        ArcoService.POLICY_VERSION);
  }

  @Test
  void proposalDoesNotChangeOriginalAndApprovalExecutesExactlyOnce() {
    var a = request(Tipo.RECTIFICACION);
    assertEquals("Original", clients.entity(client.id()).getNombre());
    assertEquals("PENDIENTE", a.estado);
    var resolved = approvals.resolver(a.id, admin, true, "Se verificó la solicitud");
    assertEquals("Actualizado", clients.entity(client.id()).getNombre());
    assertEquals("APROBADA", resolved.estado);
    assertEquals(admin, resolved.administradorId);
    assertNotNull(resolved.fechaResolucion);
    assertEquals(
        409,
        assertThrows(
                ResponseStatusException.class,
                () -> approvals.resolver(a.id, admin, true, "Segunda resolución"))
            .getStatusCode()
            .value());
  }

  @Test
  void rejectionPreservesOriginal() {
    var a = request(Tipo.CANCELACION);
    approvals.resolver(a.id, admin, false, "No corresponde eliminar");
    assertNotNull(clients.entity(client.id()));
    assertEquals("RECHAZADA", requests.findById(a.id).orElseThrow().estado);
  }

  @Test
  void deletionHappensOnlyOnApproval() {
    var a = request(Tipo.CANCELACION);
    assertNotNull(clients.entity(client.id()));
    approvals.resolver(a.id, admin, true, "Procede la eliminación");
    assertThrows(ResponseStatusException.class, () -> clients.entity(client.id()));
  }

  @Test
  void staleRecordRemainsPendingInsteadOfOverwriting() {
    var a = request(Tipo.RECTIFICACION);
    clients.actualizar(client.id(), client.version(), data("Edición posterior"));
    assertEquals(
        409,
        assertThrows(
                ResponseStatusException.class,
                () -> approvals.resolver(a.id, admin, true, "Procede la corrección"))
            .getStatusCode()
            .value());
    assertEquals("PENDIENTE", requests.findById(a.id).orElseThrow().estado);
    assertEquals("Edición posterior", clients.entity(client.id()).getNombre());
  }

  @Test
  void preventsCrossAccountRequestsAndSelfApproval() {
    assertEquals(
        403,
        assertThrows(
                ResponseStatusException.class,
                () ->
                    approvals.solicitar(
                        admin,
                        "clientes",
                        client.id(),
                        Tipo.CANCELACION,
                        client.version(),
                        null,
                        "Solicito eliminar el registro",
                        true,
                        ArcoService.POLICY_VERSION))
            .getStatusCode()
            .value());
    var a = request(Tipo.CANCELACION);
    assertEquals(
        403,
        assertThrows(
                ResponseStatusException.class,
                () -> approvals.resolver(a.id, owner, true, "Intento de autoaprobación"))
            .getStatusCode()
            .value());
  }

  @Test
  void accessAndOppositionHaveExplicitEffects() {
    var access = request(Tipo.ACCESO);
    var resolved = approvals.resolver(access.id, admin, true, "Copia de datos autorizada");
    assertTrue(resolved.resultado.contains("Original"));
    var oppose = request(Tipo.OPOSICION);
    approvals.resolver(oppose.id, admin, true, "Se acepta la oposición");
    assertFalse(clients.entity(client.id()).getRecibirPublicidad());
  }

  @Test
  void rejectsUnknownTablesMissingNoticeAndPrivilegeFields() {
    assertThrows(
        ResponseStatusException.class,
        () ->
            approvals.solicitar(
                owner,
                "ventas",
                client.id(),
                Tipo.CANCELACION,
                0L,
                null,
                "Solicitud inválida",
                true,
                ArcoService.POLICY_VERSION));
    assertThrows(
        ResponseStatusException.class,
        () ->
            approvals.solicitar(
                owner,
                "clientes",
                client.id(),
                Tipo.CANCELACION,
                0L,
                null,
                "Solicitud sin aviso",
                false,
                ArcoService.POLICY_VERSION));
    var u = accounts.findById(owner).orElseThrow();
    assertThrows(
        ResponseStatusException.class,
        () ->
            approvals.solicitar(
                owner,
                "usuarios",
                owner,
                Tipo.RECTIFICACION,
                u.version,
                "{\"rol\":\"ROLE_ADMIN\"}",
                "Intento de cambiar permisos",
                true,
                ArcoService.POLICY_VERSION));
  }

  @Test
  void pendingRequestCanBeEditedAndWithdrawnButNotApprovedAfterwards() {
    var a = request(Tipo.CANCELACION);
    var edited = approvals.editar(a.id, owner, a.version, "Cambio de descripción de solicitud");
    assertEquals("Cambio de descripción de solicitud", edited.descripcion);
    approvals.retirar(a.id, owner, edited.version);
    assertNotNull(clients.entity(client.id()));
    assertEquals(
        409,
        assertThrows(
                ResponseStatusException.class,
                () -> approvals.resolver(a.id, admin, true, "Intento tras retiro"))
            .getStatusCode()
            .value());
  }

  @Test
  @WithMockUser(roles = "USER")
  void regularUserCannotResolve() {
    var a = request(Tipo.CANCELACION);
    assertThrows(
        org.springframework.security.access.AccessDeniedException.class,
        () -> approvals.resolver(a.id, admin, true, "Intento sin rol admin"));
  }
}

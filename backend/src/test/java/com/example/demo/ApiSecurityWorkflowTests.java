package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.aplicacion.servicio.AuthUseCase;
import com.example.demo.infraestructura.adaptadores.salida.bd.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiSecurityWorkflowTests {
  @Value("${local.server.port}")
  int port;

  @Autowired AuthUseCase auth;
  @Autowired SpringDataUsuarioRepository users;
  @Autowired SpringDataClienteRepository clients;
  @Autowired SpringDataArcoRepository requests;
  @Autowired JsonMapper json;
  @Autowired SpringDataVehicleRepository vehicleDb;
  @Autowired SpringDataSaleRepository saleDb;
  final HttpClient http = HttpClient.newHttpClient();

  HttpResponse<String> call(String method, String path, String token, Object body)
      throws Exception {
    var b =
        HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
            .header("Accept", "application/json");
    if (token != null) b.header("Authorization", "Bearer " + token);
    b.method(
        method,
        body == null
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
    if (body != null) b.header("Content-Type", "application/json");
    return http.send(b.build(), HttpResponse.BodyHandlers.ofString());
  }

  JsonNode expect(int code, HttpResponse<String> r) {
    assertEquals(code, r.statusCode(), r.body());
    return r.body().isBlank() ? null : json.readTree(r.body());
  }

  String login(String email) throws Exception {
    return expect(
            200,
            call(
                "POST",
                "/api/auth/login",
                null,
                Map.of("correo", email, "password", "ClaveSegura123")))
        .get("accessToken")
        .asString();
  }

  @Test
  void realBearerRequestsEnforceRolesOwnershipAndApprovalWithoutSessions() throws Exception {
    String tag = UUID.randomUUID().toString();
    String uEmail = "http-u-" + tag + "@example.invalid",
        aEmail = "http-a-" + tag + "@example.invalid";
    var u = auth.registrar("Cliente HTTP", uEmail, "5551234567", "ClaveSegura123");
    var a = auth.registrar("Admin HTTP", aEmail, "5551234567", "ClaveSegura123");
    var admin = users.findById(a.getId()).orElseThrow();
    admin.setRol("ROLE_ADMIN");
    users.saveAndFlush(admin);
    Long clientId = null,
        requestId = null,
        createdUserId = null,
        vehicleId = null,
        saleId = null,
        cancelId = null;
    try {
      expect(401, call("GET", "/api/clientes", null, null));
      expect(401, call("GET", "/api/clientes", "invalid.jwt.token", null));
      expect(
          401,
          call(
              "POST",
              "/api/auth/login",
              null,
              Map.of("correo", uEmail, "password", "Incorrecta123")));
      String userToken = login(uEmail), adminToken = login(aEmail);
      var me = call("GET", "/api/auth/me", userToken, null);
      expect(200, me);
      assertTrue(me.headers().allValues("set-cookie").isEmpty());
      expect(403, call("GET", "/api/usuarios", userToken, null));
      expect(403, call("GET", "/api/ventas", userToken, null));
      expect(403, call("GET", "/api/admin/arco-requests", userToken, null));
      expect(403, call("GET", "/api/vehiculos", userToken, null));
      var create =
          new HashMap<String, Object>(
              Map.of(
                  "nombre",
                  "Original",
                  "apellido",
                  "HTTP",
                  "rfc",
                  "XAXX010101000",
                  "direccion",
                  "Calle Prueba 123",
                  "telefono",
                  "5551234567",
                  "correo",
                  uEmail,
                  "recibirPublicidad",
                  true));
      var c = expect(201, call("POST", "/api/clientes", userToken, create));
      clientId = c.get("id").asLong();
      assertEquals(u.getId().longValue(), c.get("usuarioId").asLong());
      expect(
          400,
          call(
              "PUT",
              "/api/clientes/" + clientId,
              userToken,
              Map.of(
                  "version",
                  c.get("version").asLong(),
                  "datos",
                  create,
                  "descripcion",
                  "Sin confirmación de aviso",
                  "avisoLeido",
                  false,
                  "avisoVersion",
                  "academico-1")));
      create.put("nombre", "Actualizado");
      var proposal =
          expect(
              202,
              call(
                  "PUT",
                  "/api/clientes/" + clientId,
                  userToken,
                  Map.of(
                      "version",
                      c.get("version").asLong(),
                      "datos",
                      create,
                      "descripcion",
                      "Solicito corregir el nombre",
                      "avisoLeido",
                      true,
                      "avisoVersion",
                      "academico-1")));
      requestId = proposal.get("id").asLong();
      assertEquals(
          "Original",
          expect(200, call("GET", "/api/clientes/" + clientId, userToken, null))
              .get("nombre")
              .asString());
      expect(
          403,
          call(
              "POST",
              "/api/admin/arco-requests/" + requestId + "/resolve",
              userToken,
              Map.of("decision", "APROBAR", "motivo", "Intento sin permisos")));
      expect(
          400,
          call(
              "POST",
              "/api/admin/arco-requests/" + requestId + "/resolve",
              adminToken,
              Map.of("motivo", "Decisión ausente")));
      var resolved =
          expect(
              200,
              call(
                  "POST",
                  "/api/admin/arco-requests/" + requestId + "/resolve",
                  adminToken,
                  Map.of("decision", "APROBAR", "motivo", "Solicitud verificada")));
      assertEquals("APROBADA", resolved.get("estado").asString());
      assertEquals(
          "Actualizado",
          expect(200, call("GET", "/api/clientes/" + clientId, userToken, null))
              .get("nombre")
              .asString());
      expect(
          409,
          call(
              "POST",
              "/api/admin/arco-requests/" + requestId + "/resolve",
              adminToken,
              Map.of("decision", "APROBAR", "motivo", "Segunda aprobación")));
      var newUser =
          expect(
              201,
              call(
                  "POST",
                  "/api/usuarios",
                  adminToken,
                  Map.of(
                      "nombreUsuario",
                      "Creado por admin",
                      "correo",
                      "http-new-" + tag + "@example.invalid",
                      "telefono",
                      "5551234567",
                      "password",
                      "ClaveSegura123",
                      "rol",
                      "ROLE_USER")));
      createdUserId = newUser.get("id").asLong();
      assertNull(newUser.get("password"));
      assertTrue(
          new BCryptPasswordEncoder()
              .matches(
                  "ClaveSegura123", users.findById(createdUserId).orElseThrow().getPassword()));
      String newToken = login("http-new-" + tag + "@example.invalid");
      expect(403, call("GET", "/api/clientes/" + clientId, newToken, null));

      var vehicle =
          expect(
              201,
              call(
                  "POST",
                  "/api/vehicles",
                  adminToken,
                  Map.of(
                      "marca",
                      "Toyota",
                      "modelo",
                      "Corolla",
                      "anio",
                      2024,
                      "vin",
                      "1HGCM82633A004357",
                      "precio",
                      200000,
                      "kilometraje",
                      100,
                      "estado",
                      "DISPONIBLE")));
      vehicleId = vehicle.get("id").asLong();
      var sale =
          expect(
              201,
              call(
                  "POST",
                  "/api/ventas",
                  adminToken,
                  Map.of(
                      "clienteId",
                      clientId,
                      "vehiculoId",
                      vehicleId,
                      "montoTotal",
                      200000,
                      "metodoPago",
                      "TRANSFERENCIA")));
      saleId = sale.get("id").asLong();
      var clientNow = expect(200, call("GET", "/api/clientes/" + clientId, userToken, null));
      var cancel =
          expect(
              202,
              call(
                  "POST",
                  "/api/arco-requests/actions",
                  userToken,
                  Map.of(
                      "tablaAfectada",
                      "clientes",
                      "registroId",
                      clientId,
                      "registroVersion",
                      clientNow.get("version").asLong(),
                      "tipo",
                      "CANCELACION",
                      "descripcion",
                      "Solicito eliminar mis datos",
                      "avisoLeido",
                      true,
                      "avisoVersion",
                      "academico-1")));
      cancelId = cancel.get("id").asLong();
      expect(
          409,
          call(
              "POST",
              "/api/admin/arco-requests/" + cancelId + "/resolve",
              adminToken,
              Map.of("decision", "APROBAR", "motivo", "Intento de eliminar cliente con venta")));
      assertEquals(
          "PENDIENTE",
          expect(200, call("GET", "/api/arco-requests/" + cancelId, userToken, null))
              .get("estado")
              .asString());
      expect(200, call("GET", "/api/clientes/" + clientId, userToken, null));
      // A demoted admin must obtain a new token; the old authority claim is rejected.
      var demoted = users.findById(a.getId()).orElseThrow();
      demoted.setRol("ROLE_USER");
      users.saveAndFlush(demoted);
      expect(401, call("GET", "/api/admin/arco-requests", adminToken, null));
      var preflight =
          HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/clientes"))
              .header("Origin", "http://agencia.local:4200")
              .header("Access-Control-Request-Method", "PUT")
              .header("Access-Control-Request-Headers", "authorization,content-type")
              .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
              .build();
      var cors = http.send(preflight, HttpResponse.BodyHandlers.ofString());
      assertEquals(200, cors.statusCode());
      assertEquals(
          "http://agencia.local:4200",
          cors.headers().firstValue("access-control-allow-origin").orElseThrow());
    } finally {
      if (saleId != null) saleDb.deleteById(saleId);
      if (vehicleId != null) vehicleDb.deleteById(vehicleId);
      if (cancelId != null) requests.deleteById(cancelId);
      if (requestId != null) requests.deleteById(requestId);
      if (clientId != null) clients.deleteById(clientId);
      if (createdUserId != null) users.deleteById(createdUserId);
      users.deleteById(u.getId());
      users.deleteById(a.getId());
    }
  }
}

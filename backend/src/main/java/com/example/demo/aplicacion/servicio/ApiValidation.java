package com.example.demo.aplicacion.servicio;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class ApiValidation {
  private ApiValidation() {}

  public static void require(boolean ok, String message) {
    if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
  }

  public static String text(String s, int min, int max, String name) {
    require(s != null && s.trim().length() >= min && s.trim().length() <= max, "Revisa " + name);
    return s.trim();
  }

  public static String email(String s) {
    String v = text(s, 3, 100, "correo").toLowerCase(java.util.Locale.ROOT);
    require(v.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"), "Correo inválido");
    return v;
  }

  public static String phone(String s) {
    require(s != null && s.matches("[0-9]{10}"), "Teléfono: 10 dígitos");
    return s;
  }

  public static void version(long actual, Long expected) {
    if (expected == null || actual != expected)
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "El registro cambió. Actualiza la pantalla y vuelve a solicitar.");
  }
}

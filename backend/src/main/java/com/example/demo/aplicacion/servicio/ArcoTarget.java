package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.ArcoRequest.Tipo;

public interface ArcoTarget {
  String tabla();

  long version(Long id, Long requester, boolean admin);

  String preparar(Long id, Tipo tipo, String propuesta);

  String ejecutar(Long id, Tipo tipo, String propuesta, long version);
}

package com.example.demo.infraestructura.adaptadores.salida.bd;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitudes_arco")
public class ArcoRequestEntity {
  @Version public Long version;
  public String tablaAfectada;
  public Long registroId;
  public Long registroVersion;

  @Column(length = 8000)
  public String propuesta;

  @Column(length = 8000)
  public String resultado;

  public Long administradorId;
  public java.time.Instant fechaResolucion;

  @Column(length = 1000)
  public String motivoResolucion;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false)
  public Long usuarioId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public com.example.demo.dominio.modelo.ArcoRequest.Tipo tipo;

  @Column(nullable = false, length = 4000)
  public String descripcion;

  @Column(nullable = false)
  public String estado;

  @Column(length = 1000)
  public String evidencia;

  public boolean avisoLeido;
  public String avisoVersion;
  public java.time.Instant createdAt;
}

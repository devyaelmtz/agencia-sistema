package com.example.demo.infraestructura.adaptadores.salida.bd;

import jakarta.persistence.*;

@Entity
@Table(name = "ventas")
public class SaleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(optional = false)
  @JoinColumn(name = "cliente_id")
  public ClienteEntity cliente;

  @ManyToOne(optional = false)
  @JoinColumn(name = "vehicle_id", unique = true)
  public VehicleEntity vehicle;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id")
  public UsuarioEntity usuario;

  public java.time.Instant fecha;

  @Column(precision = 15, scale = 2, nullable = false)
  public java.math.BigDecimal montoTotal;

  @Enumerated(EnumType.STRING)
  @Column(length = 30, nullable = false)
  public MetodoPago metodoPago;

  public enum MetodoPago {
    EFECTIVO,
    TRANSFERENCIA,
    TARJETA,
    FINANCIAMIENTO
  }
}

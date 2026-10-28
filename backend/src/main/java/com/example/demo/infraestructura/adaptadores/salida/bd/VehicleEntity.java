package com.example.demo.infraestructura.adaptadores.salida.bd;
import jakarta.persistence.*;
@Entity @Table(name="vehicles")
public class VehicleEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false)
    public String marca;
    @Column(nullable=false)
    public String modelo;
    @Column(nullable=false)
    public Integer anio;
    @Column(nullable=false, unique=true, length=17)
    public String vin;
    @Column(nullable=false, precision=15, scale=2)
    public java.math.BigDecimal precio;
    @Column(nullable=false)
    public Long kilometraje;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    public com.example.demo.dominio.modelo.Vehicle.Estado estado;
}

package com.example.demo.infraestructura.adaptadores.salida.bd;
import jakarta.persistence.*;
@Entity @Table(name="arco_requests")
public class ArcoRequestEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public Long usuarioId;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public com.example.demo.dominio.modelo.ArcoRequest.Tipo tipo;
    @Column(nullable=false,length=4000) public String descripcion;
    @Column(nullable=false) public String estado;
    @Column(length=1000) public String evidencia;
    public boolean avisoLeido;
    public String avisoVersion;
    public java.time.Instant createdAt;
}

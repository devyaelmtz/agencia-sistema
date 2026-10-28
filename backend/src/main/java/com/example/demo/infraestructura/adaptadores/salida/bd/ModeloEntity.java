package com.example.demo.infraestructura.adaptadores.salida.bd;

import jakarta.persistence.*;

@Entity
@Table(name = "modelos")
public class ModeloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nombre;

    @Column
    private Integer anio;

    @Column(name = "marca_id")
    private Long marcaId;

    @Column(name = "tipo_vehiculo_id")
    private Long tipoVehiculoId;

    public ModeloEntity() {}

    public ModeloEntity(Long id, String nombre, Integer anio, Long marcaId, Long tipoVehiculoId) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.marcaId = marcaId;
        this.tipoVehiculoId = tipoVehiculoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
    }

    public Long getTipoVehiculoId() {
        return tipoVehiculoId;
    }

    public void setTipoVehiculoId(Long tipoVehiculoId) {
        this.tipoVehiculoId = tipoVehiculoId;
    }
}
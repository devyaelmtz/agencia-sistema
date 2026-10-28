package com.example.demo.dominio.modelo;

public class Modelo {
    private Long id;
    private String nombre;
    private Integer anio;
    private Long marcaId;
    private Long tipoVehiculoId;

    public Modelo() {}

    public Modelo(Long id, String nombre, Integer anio, Long marcaId, Long tipoVehiculoId) {
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
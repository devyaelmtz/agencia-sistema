package com.example.demo.dominio.modelo;

public class Sucursal {
    private Long id;
    private String nombre;
    private String direccion;
    private Long estadoId;

    public Sucursal() {}

    public Sucursal(Long id, String nombre, String direccion, Long estadoId) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.estadoId = estadoId;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }
}
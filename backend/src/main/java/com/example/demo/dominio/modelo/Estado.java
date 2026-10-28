package com.example.demo.dominio.modelo;

public class Estado {
    private Long id;
    private String nombre;
    private Long paisId;

    public Estado() {}

    public Estado(Long id, String nombre, Long paisId) {
        this.id = id;
        this.nombre = nombre;
        this.paisId = paisId;
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

    public Long getPaisId() {
        return paisId;
    }

    public void setPaisId(Long paisId) {
        this.paisId = paisId;
    }
}
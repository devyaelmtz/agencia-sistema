package com.example.demo.dominio.modelo;

public class Vehiculo {
    private Long id;
    private String vin;
    private String color;
    private Double precio;
    private String estadoVehiculo;

    public Vehiculo() {}

    public Vehiculo(Long id, String vin, String color, Double precio, String estadoVehiculo) {
        this.id = id;
        this.vin = vin;
        this.color = color;
        this.precio = precio;
        this.estadoVehiculo = estadoVehiculo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getEstadoVehiculo() { return estadoVehiculo; }
    public void setEstadoVehiculo(String estadoVehiculo) { this.estadoVehiculo = estadoVehiculo; }
}
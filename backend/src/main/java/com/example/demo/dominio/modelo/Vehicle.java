package com.example.demo.dominio.modelo;
import java.math.BigDecimal;
public record Vehicle(Long id, String marca, String modelo, Integer anio, String vin, BigDecimal precio, Long kilometraje, Estado estado) {
    public enum Estado { DISPONIBLE, VENDIDO, MANTENIMIENTO }
}

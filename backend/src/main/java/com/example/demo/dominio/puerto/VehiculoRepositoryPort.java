package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Vehiculo;
import java.util.List;

public interface VehiculoRepositoryPort {
    List<Vehiculo> findAll();
    List<Vehiculo> obtenerPorColor(String color);
    Vehiculo guardar(Vehiculo vehiculo);
    Vehiculo findById(Long id);
    void eliminar(Long id);
}
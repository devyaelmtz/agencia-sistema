package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Vehiculo;
import com.example.demo.dominio.puerto.VehiculoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoUseCase {

    private final VehiculoRepositoryPort vehiculoRepositoryPort;

    public VehiculoUseCase(VehiculoRepositoryPort vehiculoRepositoryPort) {
        this.vehiculoRepositoryPort = vehiculoRepositoryPort;
    }

    public List<Vehiculo> obtenerPorColor(String color) {
        return vehiculoRepositoryPort.obtenerPorColor(color);
    }

    public Vehiculo buscarPorId(Long id) {
        return vehiculoRepositoryPort.findById(id);
    }

    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepositoryPort.guardar(vehiculo);
    }

    // --- AGREGA ESTE MÉTODO NUEVO ---
    public void eliminar(Long id) {
        vehiculoRepositoryPort.eliminar(id); // O el nombre del método de borrado que tenga tu puerto/repositorio
    }
}
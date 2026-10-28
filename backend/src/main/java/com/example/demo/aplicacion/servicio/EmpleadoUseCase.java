package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Empleado;
import com.example.demo.dominio.puerto.EmpleadoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoUseCase {

    private final EmpleadoRepositoryPort empleadoRepositoryPort;

    public EmpleadoUseCase(EmpleadoRepositoryPort empleadoRepositoryPort) {
        this.empleadoRepositoryPort = empleadoRepositoryPort;
    }

    public List<Empleado> listarTodos() {
        return empleadoRepositoryPort.findAll();
    }

    public Empleado buscarPorId(Long id) {
        return empleadoRepositoryPort.findById(id);
    }

    public Empleado guardar(Empleado empleado) {
        return empleadoRepositoryPort.guardar(empleado);
    }
}
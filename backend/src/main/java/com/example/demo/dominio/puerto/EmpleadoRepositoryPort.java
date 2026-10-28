package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Empleado;
import java.util.List;

public interface EmpleadoRepositoryPort {
    List<Empleado> findAll();
    Empleado findById(Long id);
    Empleado guardar(Empleado empleado);
}
package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Estado;
import com.example.demo.dominio.puerto.EstadoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoUseCase {

    private final EstadoRepositoryPort estadoRepositoryPort;

    public EstadoUseCase(EstadoRepositoryPort estadoRepositoryPort) {
        this.estadoRepositoryPort = estadoRepositoryPort;
    }

    public List<Estado> listarTodos() {
        return estadoRepositoryPort.findAll();
    }

    public Estado buscarPorId(Long id) {
        return estadoRepositoryPort.findById(id);
    }

    public Estado guardar(Estado estado) {
        return estadoRepositoryPort.guardar(estado);
    }
}
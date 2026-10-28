package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Modelo;
import com.example.demo.dominio.puerto.ModeloRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModeloUseCase {

    private final ModeloRepositoryPort modeloRepositoryPort;

    public ModeloUseCase(ModeloRepositoryPort modeloRepositoryPort) {
        this.modeloRepositoryPort = modeloRepositoryPort;
    }

    public List<Modelo> listarTodos() {
        return modeloRepositoryPort.findAll();
    }

    public Modelo buscarPorId(Long id) {
        return modeloRepositoryPort.findById(id);
    }

    public Modelo guardar(Modelo modelo) {
        return modeloRepositoryPort.guardar(modelo);
    }
}
package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Rol;
import com.example.demo.dominio.puerto.RolRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolUseCase {

    private final RolRepositoryPort rolRepositoryPort;

    public RolUseCase(RolRepositoryPort rolRepositoryPort) {
        this.rolRepositoryPort = rolRepositoryPort;
    }

    public List<Rol> listarTodos() {
        return rolRepositoryPort.findAll();
    }

    public Rol buscarPorId(Long id) {
        return rolRepositoryPort.findById(id);
    }

    public Rol guardar(Rol rol) {
        return rolRepositoryPort.guardar(rol);
    }
}
package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Proveedor;
import com.example.demo.dominio.puerto.ProveedorRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorUseCase {

    private final ProveedorRepositoryPort proveedorRepositoryPort;

    public ProveedorUseCase(ProveedorRepositoryPort proveedorRepositoryPort) {
        this.proveedorRepositoryPort = proveedorRepositoryPort;
    }

    public List<Proveedor> listarTodos() {
        return proveedorRepositoryPort.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepositoryPort.findById(id);
    }

    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepositoryPort.guardar(proveedor);
    }
}
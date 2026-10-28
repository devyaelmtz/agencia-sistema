package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.Sucursal;
import com.example.demo.dominio.puerto.SucursalRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalUseCase {

    private final SucursalRepositoryPort sucursalRepositoryPort;

    public SucursalUseCase(SucursalRepositoryPort sucursalRepositoryPort) {
        this.sucursalRepositoryPort = sucursalRepositoryPort;
    }

    public List<Sucursal> listarTodos() {
        return sucursalRepositoryPort.findAll();
    }

    public Sucursal buscarPorId(Long id) {
        return sucursalRepositoryPort.findById(id);
    }

    public Sucursal guardar(Sucursal sucursal) {
        return sucursalRepositoryPort.guardar(sucursal);
    }
}
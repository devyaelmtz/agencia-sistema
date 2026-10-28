package com.example.demo.aplicacion.servicio;

import com.example.demo.dominio.modelo.TipoVehiculo;
import com.example.demo.dominio.puerto.TipoVehiculoRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoVehiculoUseCase {

    private final TipoVehiculoRepositoryPort tipoVehiculoRepositoryPort;

    public TipoVehiculoUseCase(TipoVehiculoRepositoryPort tipoVehiculoRepositoryPort) {
        this.tipoVehiculoRepositoryPort = tipoVehiculoRepositoryPort;
    }

    public List<TipoVehiculo> listarTodos() {
        return tipoVehiculoRepositoryPort.findAll();
    }

    public TipoVehiculo buscarPorId(Long id) {
        return tipoVehiculoRepositoryPort.findById(id);
    }

    public TipoVehiculo guardar(TipoVehiculo tipoVehiculo) {
        return tipoVehiculoRepositoryPort.guardar(tipoVehiculo);
    }
}
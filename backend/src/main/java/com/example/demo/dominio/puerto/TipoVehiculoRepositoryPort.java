package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.TipoVehiculo;
import java.util.List;

public interface TipoVehiculoRepositoryPort {
    List<TipoVehiculo> findAll();
    TipoVehiculo findById(Long id);
    TipoVehiculo guardar(TipoVehiculo tipoVehiculo);
}
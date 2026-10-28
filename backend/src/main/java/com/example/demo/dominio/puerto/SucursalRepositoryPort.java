package com.example.demo.dominio.puerto;

import com.example.demo.dominio.modelo.Sucursal;
import java.util.List;

public interface SucursalRepositoryPort {
    List<Sucursal> findAll();
    Sucursal findById(Long id);
    Sucursal guardar(Sucursal sucursal);
}
package com.example.demo.dominio.puerto;
import com.example.demo.dominio.modelo.Vehicle;
import java.util.*;
public interface VehicleRepositoryPort {
    List<Vehicle> findAll(); Optional<Vehicle> findById(Long id); Vehicle save(Vehicle vehicle); void delete(Long id);
}

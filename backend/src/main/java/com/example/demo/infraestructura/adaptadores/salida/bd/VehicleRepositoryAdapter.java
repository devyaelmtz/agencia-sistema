package com.example.demo.infraestructura.adaptadores.salida.bd;
import com.example.demo.dominio.modelo.Vehicle;
import com.example.demo.dominio.puerto.VehicleRepositoryPort;
import java.util.*;
@org.springframework.stereotype.Component
public class VehicleRepositoryAdapter implements VehicleRepositoryPort {
    private final SpringDataVehicleRepository db;
    public VehicleRepositoryAdapter(SpringDataVehicleRepository db) { this.db=db; }
    private Vehicle map(VehicleEntity e) { return new Vehicle(e.id,e.marca,e.modelo,e.anio,e.vin,e.precio,e.kilometraje,e.estado); }
    public List<Vehicle> findAll() { return db.findAll().stream().map(this::map).toList(); }
    public Optional<Vehicle> findById(Long id) { return db.findById(id).map(this::map); }
    public Vehicle save(Vehicle v) {
        var e = new VehicleEntity(); e.id=v.id(); e.marca=v.marca(); e.modelo=v.modelo(); e.anio=v.anio(); e.vin=v.vin(); e.precio=v.precio(); e.kilometraje=v.kilometraje(); e.estado=v.estado();
        return map(db.saveAndFlush(e));
    }
    public void delete(Long id) { db.deleteById(id); }
}

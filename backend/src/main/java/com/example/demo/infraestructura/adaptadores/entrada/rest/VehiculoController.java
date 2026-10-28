package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.VehiculoUseCase;
import com.example.demo.dominio.modelo.Vehiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class VehiculoController {

    private final VehiculoUseCase useCase;

    public VehiculoController(VehiculoUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos o filtrar opcionalmente por color (?color=Rojo)
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listarVehiculos(@RequestParam(required = false) String color) {
        List<Vehiculo> lista = useCase.obtenerPorColor(color);
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar un vehículo específico por ID (/api/vehiculos/1)
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtenerPorId(@PathVariable Long id) {
        Vehiculo vehiculo = useCase.buscarPorId(id);
        if (vehiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehiculo);
    }

    // POST: Registrar / Crear un nuevo vehículo
    @PostMapping
    public ResponseEntity<Vehiculo> crearVehiculo(@RequestBody Vehiculo vehiculo) {
        Vehiculo nuevoVehiculo = useCase.guardar(vehiculo);
        return ResponseEntity.ok(nuevoVehiculo);
    }

    // PUT: Actualizar un vehículo existente por ID (/api/vehiculos/1)
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizarVehiculo(@PathVariable Long id, @RequestBody Vehiculo vehiculo) {
        vehiculo.setId(id); // Asegura que el ID de la URL sea el que se actualiza
        Vehiculo vehiculoActualizado = useCase.guardar(vehiculo);
        return ResponseEntity.ok(vehiculoActualizado);
    }

    // --- NUEVO MÉTODO PARA ELIMINAR ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {
        useCase.eliminar(id); // IMPORTANTE: Si tu método en VehiculoUseCase se llama diferente (ej. borrarPorId, eliminarVehiculo), ajusta esta palabra.
        return ResponseEntity.noContent().build();
    }
}
package com.example.demo.infraestructura.adaptadores.entrada.rest;
import com.example.demo.aplicacion.servicio.VehicleService;
import com.example.demo.dominio.modelo.Vehicle;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
@RestController @RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService service;
    public VehicleController(VehicleService service) { this.service=service; }
    @GetMapping public java.util.List<Vehicle> listar() { return service.listar(); }
    @GetMapping("/{id}") public Vehicle obtener(@PathVariable Long id) { return service.obtener(id); }
    @PostMapping public ResponseEntity<Vehicle> crear(@RequestBody Vehicle v) { var saved=service.guardar(null,v); return ResponseEntity.created(java.net.URI.create("/api/vehicles/"+saved.id())).body(saved); }
    @PutMapping("/{id}") public Vehicle actualizar(@PathVariable Long id,@RequestBody Vehicle v) { return service.guardar(id,v); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void eliminar(@PathVariable Long id) { service.eliminar(id); }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> conflicto() { return ResponseEntity.status(409).body(java.util.Map.of("message","El VIN ya existe o el vehículo está relacionado con otros datos.")); }
}

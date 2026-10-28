package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.TipoVehiculoUseCase;
import com.example.demo.dominio.modelo.TipoVehiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-vehiculo")
@CrossOrigin(origins = "*")
public class TipoVehiculoController {

    private final TipoVehiculoUseCase useCase;

    public TipoVehiculoController(TipoVehiculoUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los tipos de vehículo
    @GetMapping
    public ResponseEntity<List<TipoVehiculo>> listarTiposVehiculo() {
        List<TipoVehiculo> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar tipo de vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculo> obtenerPorId(@PathVariable Long id) {
        TipoVehiculo tipoVehiculo = useCase.buscarPorId(id);
        if (tipoVehiculo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tipoVehiculo);
    }

    // POST: Crear un nuevo tipo de vehículo
    @PostMapping
    public ResponseEntity<TipoVehiculo> crearTipoVehiculo(@RequestBody TipoVehiculo tipoVehiculo) {
        TipoVehiculo nuevoTipo = useCase.guardar(tipoVehiculo);
        return ResponseEntity.ok(nuevoTipo);
    }

    // PUT: Actualizar un tipo de vehículo existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<TipoVehiculo> actualizarTipoVehiculo(@PathVariable Long id, @RequestBody TipoVehiculo tipoVehiculo) {
        tipoVehiculo.setId(id);
        TipoVehiculo tipoActualizado = useCase.guardar(tipoVehiculo);
        return ResponseEntity.ok(tipoActualizado);
    }
}
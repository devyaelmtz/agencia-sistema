package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.EstadoUseCase;
import com.example.demo.dominio.modelo.Estado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
@CrossOrigin(origins = "*")
public class EstadoController {

    private final EstadoUseCase useCase;

    public EstadoController(EstadoUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los estados
    @GetMapping
    public ResponseEntity<List<Estado>> listarEstados() {
        List<Estado> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar estado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Estado> obtenerPorId(@PathVariable Long id) {
        Estado estado = useCase.buscarPorId(id);
        if (estado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(estado);
    }

    // POST: Crear un nuevo estado (asociado a un país existente por su ID)
    @PostMapping
    public ResponseEntity<Estado> crearEstado(@RequestBody Estado estado) {
        Estado nuevoEstado = useCase.guardar(estado);
        return ResponseEntity.ok(nuevoEstado);
    }

    // PUT: Actualizar un estado existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Estado> actualizarEstado(@PathVariable Long id, @RequestBody Estado estado) {
        estado.setId(id);
        Estado estadoActualizado = useCase.guardar(estado);
        return ResponseEntity.ok(estadoActualizado);
    }
}

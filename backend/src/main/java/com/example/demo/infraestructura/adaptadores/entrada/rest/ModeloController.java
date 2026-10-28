package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.ModeloUseCase;
import com.example.demo.dominio.modelo.Modelo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
@CrossOrigin(origins = "*")
public class ModeloController {

    private final ModeloUseCase useCase;

    public ModeloController(ModeloUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los modelos
    @GetMapping
    public ResponseEntity<List<Modelo>> listarModelos() {
        List<Modelo> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar modelo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Modelo> obtenerPorId(@PathVariable Long id) {
        Modelo modelo = useCase.buscarPorId(id);
        if (modelo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelo);
    }

    // POST: Crear un nuevo modelo (asociado a marcaId y tipoVehiculoId previos)
    @PostMapping
    public ResponseEntity<Modelo> crearModelo(@RequestBody Modelo modelo) {
        Modelo nuevoModelo = useCase.guardar(modelo);
        return ResponseEntity.ok(nuevoModelo);
    }

    // PUT: Actualizar un modelo existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Modelo> actualizarModelo(@PathVariable Long id, @RequestBody Modelo modelo) {
        modelo.setId(id);
        Modelo modeloActualizado = useCase.guardar(modelo);
        return ResponseEntity.ok(modeloActualizado);
    }
}
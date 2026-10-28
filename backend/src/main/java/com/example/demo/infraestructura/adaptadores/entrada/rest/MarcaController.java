package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.MarcaUseCase;
import com.example.demo.dominio.modelo.Marca;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@CrossOrigin(origins = "*")
public class MarcaController {

    private final MarcaUseCase useCase;

    public MarcaController(MarcaUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todas las marcas
    @GetMapping
    public ResponseEntity<List<Marca>> listarMarcas() {
        List<Marca> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar marca por ID
    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(@PathVariable Long id) {
        Marca marca = useCase.buscarPorId(id);
        if (marca == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(marca);
    }

    // POST: Crear una nueva marca
    @PostMapping
    public ResponseEntity<Marca> crearMarca(@RequestBody Marca marca) {
        Marca nuevaMarca = useCase.guardar(marca);
        return ResponseEntity.ok(nuevaMarca);
    }

    // PUT: Actualizar una marca existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Marca> actualizarMarca(@PathVariable Long id, @RequestBody Marca marca) {
        marca.setId(id);
        Marca marcaActualizada = useCase.guardar(marca);
        return ResponseEntity.ok(marcaActualizada);
    }
}
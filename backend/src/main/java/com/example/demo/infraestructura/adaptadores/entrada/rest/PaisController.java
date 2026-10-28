package com.example.demo.infraestructura.adaptadores.entrada.rest;

import com.example.demo.aplicacion.servicio.PaisUseCase;
import com.example.demo.dominio.modelo.Pais;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paises")
@CrossOrigin(origins = "*")
public class PaisController {

    private final PaisUseCase useCase;

    public PaisController(PaisUseCase useCase) {
        this.useCase = useCase;
    }

    // GET: Listar todos los países
    @GetMapping
    public ResponseEntity<List<Pais>> listarPaises() {
        List<Pais> lista = useCase.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // GET: Buscar país por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pais> obtenerPorId(@PathVariable Long id) {
        Pais pais = useCase.buscarPorId(id);
        if (pais == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pais);
    }

    // POST: Crear un nuevo país
    @PostMapping
    public ResponseEntity<Pais> crearPais(@RequestBody Pais pais) {
        Pais nuevoPais = useCase.guardar(pais);
        return ResponseEntity.ok(nuevoPais);
    }

    // PUT: Actualizar un país existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Pais> actualizarPais(@PathVariable Long id, @RequestBody Pais pais) {
        pais.setId(id);
        Pais paisActualizado = useCase.guardar(pais);
        return ResponseEntity.ok(paisActualizado);
    }
}
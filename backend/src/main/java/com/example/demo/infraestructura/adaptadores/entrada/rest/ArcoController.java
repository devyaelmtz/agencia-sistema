package com.example.demo.infraestructura.adaptadores.entrada.rest;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.*;
import com.example.demo.aplicacion.servicio.ArcoService;
import com.example.demo.dominio.modelo.ArcoRequest;
@RestController @RequestMapping("/api/arco-requests")
public class ArcoController {
    private final ArcoService service;
    public ArcoController(ArcoService service) {this.service=service;}
    public record Solicitud(ArcoRequest.Tipo tipo,String descripcion,String evidencia,boolean avisoLeido,String avisoVersion) {}
    @GetMapping public java.util.List<ArcoRequest> listar(@AuthenticationPrincipal Jwt jwt) {return service.listar(Long.valueOf(jwt.getSubject()));}
    @PostMapping public ResponseEntity<ArcoRequest> crear(@AuthenticationPrincipal Jwt jwt,@RequestBody Solicitud r) {return ResponseEntity.status(201).body(service.crear(Long.valueOf(jwt.getSubject()),r.tipo(),r.descripcion(),r.evidencia(),r.avisoLeido(),r.avisoVersion()));}
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(IllegalArgumentException e) {return ResponseEntity.badRequest().body(java.util.Map.of("message",e.getMessage()));}
}

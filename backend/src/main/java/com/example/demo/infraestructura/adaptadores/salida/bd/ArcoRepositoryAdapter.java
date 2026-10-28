package com.example.demo.infraestructura.adaptadores.salida.bd;
import com.example.demo.dominio.modelo.ArcoRequest;
@org.springframework.stereotype.Component
public class ArcoRepositoryAdapter implements com.example.demo.dominio.puerto.ArcoRepositoryPort {
    private final SpringDataArcoRepository db;
    public ArcoRepositoryAdapter(SpringDataArcoRepository db) {this.db=db;}
    private ArcoRequest map(ArcoRequestEntity e) {return new ArcoRequest(e.id,e.usuarioId,e.tipo,e.descripcion,e.estado,e.evidencia,e.avisoLeido,e.avisoVersion,e.createdAt);}
    public ArcoRequest guardar(ArcoRequest r) {var e=new ArcoRequestEntity();e.usuarioId=r.usuarioId();e.tipo=r.tipo();e.descripcion=r.descripcion();e.estado=r.estado();e.evidencia=r.evidencia();e.avisoLeido=r.avisoLeido();e.avisoVersion=r.avisoVersion();e.createdAt=r.createdAt();return map(db.saveAndFlush(e));}
    public java.util.List<ArcoRequest> listar(Long id) {return db.findByUsuarioIdOrderByCreatedAtDesc(id).stream().map(this::map).toList();}
}

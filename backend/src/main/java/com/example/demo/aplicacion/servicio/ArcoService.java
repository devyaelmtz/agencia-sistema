package com.example.demo.aplicacion.servicio;
import com.example.demo.dominio.modelo.ArcoRequest;
import com.example.demo.dominio.puerto.*;
@org.springframework.stereotype.Service
public class ArcoService {
    public static final String POLICY_VERSION="academico-1";
    private final ArcoRepositoryPort db;private final UsuarioRepositoryPort users;
    public ArcoService(ArcoRepositoryPort db,UsuarioRepositoryPort users) {this.db=db;this.users=users;}
    private void usuario(Long id) {if(users.findById(id)==null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED);}
    public java.util.List<ArcoRequest> listar(Long id) {usuario(id);return db.listar(id);}
    public ArcoRequest crear(Long id,ArcoRequest.Tipo tipo,String descripcion,String evidencia,boolean leido,String version) {
        usuario(id);
        if(tipo==null || descripcion==null || descripcion.trim().length()<10 || descripcion.length()>4000 || (evidencia!=null && evidencia.length()>1000)) throw new IllegalArgumentException("Selecciona el derecho y describe tu solicitud (10–4000 caracteres). Evidencia: máximo 1000 caracteres.");
        if(!leido || !POLICY_VERSION.equals(version)) throw new IllegalArgumentException("Confirma la lectura del aviso de privacidad vigente.");
        return db.guardar(new ArcoRequest(null,id,tipo,descripcion.trim(),"PENDIENTE",evidencia,true,POLICY_VERSION,java.time.Instant.now()));
    }
}

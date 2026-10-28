package com.example.demo.dominio.modelo;
import java.time.Instant;
public record ArcoRequest(Long id,Long usuarioId,Tipo tipo,String descripcion,String estado,String evidencia,boolean avisoLeido,String avisoVersion,Instant createdAt) {
    public enum Tipo { ACCESO, RECTIFICACION, CANCELACION, OPOSICION }
}

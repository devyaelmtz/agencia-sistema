package com.example.demo.aplicacion.servicio;
import com.example.demo.dominio.modelo.Vehicle;
import com.example.demo.dominio.puerto.VehicleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
public class VehicleService {
    private final VehicleRepositoryPort db;
    public VehicleService(VehicleRepositoryPort db) { this.db=db; }
    public java.util.List<Vehicle> listar() { return db.findAll(); }
    public Vehicle obtener(Long id) { return db.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Vehículo no encontrado")); }
    public Vehicle guardar(Long id, Vehicle v) {
        if (id != null) obtener(id);
        if (v.marca()==null || v.marca().isBlank() || v.marca().length()>100 || v.modelo()==null || v.modelo().isBlank() || v.modelo().length()>100 || v.anio()==null || v.anio()<1886 || v.anio()>java.time.Year.now().getValue()+1 || v.vin()==null || !v.vin().matches("[A-HJ-NPR-Z0-9]{17}") || v.precio()==null || v.precio().signum()<0 || v.precio().precision()-v.precio().scale()>13 || v.precio().scale()>2 || v.kilometraje()==null || v.kilometraje()<0 || v.estado()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Revisa los campos: VIN de 17 caracteres, año válido y cantidades no negativas.");
        return db.save(new Vehicle(id,v.marca().trim(),v.modelo().trim(),v.anio(),v.vin(),v.precio(),v.kilometraje(),v.estado()));
    }
    public void eliminar(Long id) { obtener(id); db.delete(id); }
}

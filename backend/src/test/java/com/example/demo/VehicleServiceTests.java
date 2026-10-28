package com.example.demo;
import com.example.demo.aplicacion.servicio.VehicleService;
import com.example.demo.dominio.modelo.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @Transactional
class VehicleServiceTests {
    @Autowired VehicleService service;
    Vehicle vehicle(String vin) { return new Vehicle(null,"Toyota","Corolla",2024,vin,new BigDecimal("250000.50"),1000L,Vehicle.Estado.DISPONIBLE); }
    @Test void persistsUpdatesAndDeletes() {
        var v=service.guardar(null,vehicle("1HGCM82633A004352"));
        assertEquals("Toyota",service.obtener(v.id()).marca());
        var updated=new Vehicle(v.id(),v.marca(),v.modelo(),v.anio(),v.vin(),v.precio(),2000L,Vehicle.Estado.VENDIDO);
        assertEquals(Vehicle.Estado.VENDIDO,service.guardar(v.id(),updated).estado());
        service.eliminar(v.id());
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.obtener(v.id()));
    }
    @Test void rejectsInvalidVinAndDuplicate() {
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.guardar(null,vehicle("bad")));
        service.guardar(null,vehicle("1HGCM82633A004353"));
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class,()->service.guardar(null,vehicle("1HGCM82633A004353")));
    }
}

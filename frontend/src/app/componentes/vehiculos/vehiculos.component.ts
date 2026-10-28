import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VehiculoService } from '../../servicios/vehiculo.service';
import { Vehiculo } from '../../modelos/vehiculo';

@Component({
  selector: 'app-vehiculos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './vehiculos.component.html',
  styleUrl: './vehiculos.component.css'
})
export class VehiculosComponent implements OnInit {
  listaVehiculos: Vehiculo[] = [];
  vehiculosFiltrados: Vehiculo[] = []; // Arreglo para la tabla visual
  filtroTexto: string = '';
  
  nuevoVehiculo: Vehiculo = { vin: '', color: '', precio: 0, estadoVehiculo: 'Disponible' };
  editando: boolean = false; // Controla si el formulario guarda o actualiza

  constructor(
    private vehiculoService: VehiculoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarVehiculos();
  }

  cargarVehiculos(): void {
    this.vehiculoService.obtenerVehiculos().subscribe({
      next: (datos: Vehiculo[]) => {
        this.listaVehiculos = datos;
        this.aplicarFiltro(); // Aplica filtro a los datos recién llegados
        this.cdr.detectChanges(); 
      },
      error: (err: any) => console.error('Error al cargar vehículos', err)
    });
  }

  // --- LÓGICA DEL BUSCADOR ---
  aplicarFiltro(): void {
    const texto = this.filtroTexto.toLowerCase();
    this.vehiculosFiltrados = this.listaVehiculos.filter(v => 
      v.vin.toLowerCase().includes(texto) || 
      v.color.toLowerCase().includes(texto) || 
      v.estadoVehiculo.toLowerCase().includes(texto)
    );
  }

  // --- LÓGICA DE GUARDAR / ACTUALIZAR ---
  guardarVehiculo(): void {
    if (this.editando && this.nuevoVehiculo.id) {
      this.vehiculoService.actualizarVehiculo(this.nuevoVehiculo.id, this.nuevoVehiculo).subscribe({
        next: () => {
          alert('Vehículo actualizado correctamente');
          this.cargarVehiculos();
          this.cancelarEdicion();
        },
        error: (err: any) => console.error('Error al actualizar', err)
      });
    } else {
      this.vehiculoService.crearVehiculo(this.nuevoVehiculo).subscribe({
        next: () => {
          alert('Vehículo guardado correctamente');
          this.cargarVehiculos(); 
          this.cancelarEdicion();
        },
        error: (err: any) => console.error('Error al guardar', err)
      });
    }
  }

  // --- LÓGICA DE BOTONES DE ACCIÓN ---
  editarVehiculo(vehiculo: Vehiculo): void {
    this.nuevoVehiculo = { ...vehiculo }; // Clona los datos al formulario
    this.editando = true; // Cambia el modo del formulario
    this.cdr.detectChanges(); // <-- OBLIGA A LA PANTALLA A ACTUALIZARSE
  }

  eliminarVehiculo(id: number | undefined): void {
    if (id && confirm('¿Estás seguro de eliminar este vehículo?')) {
      this.vehiculoService.eliminarVehiculo(id).subscribe({
        next: () => {
          alert('Vehículo eliminado');
          this.cargarVehiculos();
        },
        error: (err: any) => console.error('Error al eliminar', err)
      });
    }
  }

  cancelarEdicion(): void {
    this.nuevoVehiculo = { vin: '', color: '', precio: 0, estadoVehiculo: 'Disponible' };
    this.editando = false;
    this.cdr.detectChanges(); // <-- OBLIGA A LA PANTALLA A ACTUALIZARSE
  }
}
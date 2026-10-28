import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../servicios/cliente.service';
import { Cliente } from '../../modelos/cliente';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.css'
})
export class ClientesComponent implements OnInit {
  listaClientes: Cliente[] = [];

  nuevoCliente: Cliente = {
    nombre: '',
    apellido: '',
    correo: '', // <-- Cambiado de email a correo
    telefono: '',
    direccion: '', // <-- Se agregó para coincidir con la base de datos
    recibirPublicidad: true // <-- Valor por defecto para Derechos ARCO
  };

  constructor(
    private clienteService: ClienteService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarClientes();
  }

  cargarClientes(): void {
    this.clienteService.obtenerClientes().subscribe({
      next: (datos: Cliente[]) => {
        this.listaClientes = datos;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error('Error al cargar clientes', err)
    });
  }

  guardarCliente(): void {
    // 1. Validar que los campos obligatorios no estén vacíos
    if (!this.nuevoCliente.nombre || !this.nuevoCliente.apellido || !this.nuevoCliente.correo || !this.nuevoCliente.telefono) {
      alert('Por favor, completa todos los campos obligatorios antes de guardar.');
      return; // Detiene la ejecución aquí y evita que viaje al backend
    }

    // 2. Validar estrictamente que el teléfono tenga 10 dígitos numéricos
    const telefonoRegex = /^[0-9]{10}$/;
    if (!telefonoRegex.test(this.nuevoCliente.telefono)) {
      alert('El número de teléfono debe contener exactamente 10 números.');
      return;
    }

    // Si pasa todas las validaciones, procede a guardar en la base de datos
    this.clienteService.crearCliente(this.nuevoCliente).subscribe({
      next: () => {
        alert('Cliente guardado correctamente');
        this.cargarClientes();
        // Limpiamos el formulario
        this.nuevoCliente = { nombre: '', apellido: '', correo: '', telefono: '', direccion: '', recibirPublicidad: true };
      },
      error: (err: any) => console.error('Error al guardar cliente', err)
    });
  }
}
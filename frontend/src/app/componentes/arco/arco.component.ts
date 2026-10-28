import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService } from '../../servicios/cliente.service';
import { Cliente } from '../../modelos/cliente';

@Component({
  selector: 'app-arco',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './arco.component.html',
  styleUrl: './arco.component.css'
})
export class ArcoComponent implements OnInit {
  miPerfil: Cliente | null = null;
  idUsuarioActual: number = 0;

  constructor(
    private clienteService: ClienteService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Leemos quién inició sesión
    const idGuardado = localStorage.getItem('idUsuarioActual');

    if (idGuardado) {
      this.idUsuarioActual = parseInt(idGuardado, 10);
      this.cargarMisDatos(); // Llamamos a la base de datos
    } else {
      alert('Debes iniciar sesión para ejercer tus derechos ARCO.');
      this.router.navigate(['/login']);
    }
  }

  // --- DERECHO DE ACCESO ---
  cargarMisDatos(): void {
    this.clienteService.obtenerCliente(this.idUsuarioActual).subscribe({
      next: (datos: Cliente) => {
        this.miPerfil = datos;
        if (this.miPerfil.recibirPublicidad === undefined) {
          this.miPerfil.recibirPublicidad = true;
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar mis datos', err)
    });
  }

  // --- DERECHO DE RECTIFICACIÓN Y OPOSICIÓN ---
  guardarCambios(): void {
    if (this.miPerfil && this.miPerfil.id) {
      this.clienteService.actualizarCliente(this.miPerfil.id, this.miPerfil).subscribe({
        next: () => {
          alert('Tus datos y preferencias de privacidad han sido actualizados correctamente.');
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error al actualizar', err)
      });
    }
  }

  // --- DERECHO DE CANCELACIÓN ---
  eliminarCuenta(): void {
    if (confirm('¿Estás completamente seguro de eliminar tu cuenta? Esta acción borrará todos tus datos de nuestros sistemas (Derecho de Cancelación).')) {
      this.clienteService.eliminarCliente(this.idUsuarioActual).subscribe({
        next: () => {
          alert('Tus datos han sido eliminados de la plataforma. Cerrando sesión...');

          // 1. Borramos el rastro del usuario en el navegador (Cierra la sesión)
          localStorage.removeItem('idUsuarioActual');

          // 2. Redirigimos automáticamente a la pantalla de inicio de sesión
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Error al eliminar cuenta', err);
          alert('Hubo un problema al intentar eliminar tus datos.');
        }
      });
    }
  }

  // --- CERRAR SESIÓN ---
  cerrarSesion(): void {
    localStorage.removeItem('idUsuarioActual');
    this.router.navigate(['/login']);
  }
}
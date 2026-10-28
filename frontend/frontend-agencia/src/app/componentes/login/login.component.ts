import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClienteService } from '../../servicios/cliente.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  correo: string = '';
  telefono: string = '';
  mensajeError: string = '';

  constructor(private clienteService: ClienteService, private router: Router) { }

  iniciarSesion(): void {
    this.clienteService.login(this.correo, this.telefono).subscribe({
      next: (cliente) => {
        if (cliente && cliente.id) {
          // Guardamos el ID del usuario en el navegador
          localStorage.setItem('idUsuarioActual', cliente.id.toString());
          this.router.navigate(['/arco']); // Lo enviamos a la pantalla ARCO
        }
      },
      error: () => {
        this.mensajeError = 'Credenciales incorrectas. Verifica tu correo y teléfono.';
      }
    });
  }
}
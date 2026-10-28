import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../servicios/auth.service';
@Component({selector: 'app-dashboard', standalone: true, imports: [CommonModule], template: `<section class="auth-card"><h1>Inicio</h1><p *ngIf="nombre">Bienvenido, {{ nombre }}.</p><p *ngIf="error" role="alert">{{ error }}</p><button (click)="logout()">Cerrar sesión</button></section>`})
export class DashboardComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  nombre = '';
  error = '';
  constructor() { this.auth.me().subscribe({next: perfil => this.nombre = perfil.nombreUsuario, error: () => this.error = 'No fue posible cargar tu perfil.'}); }
  logout() { this.auth.logout(); void this.router.navigate(['/login']); }
}

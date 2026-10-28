import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'] // Opcional, si tienes CSS
})
export class NavbarComponent implements OnInit {
  sesionIniciada: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // 1. Revisar si hay sesión al cargar la página por primera vez
    this.verificarSesion();

    // 2. Volver a revisar cada vez que el usuario cambie de ruta (ej. de /login a /arco)
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.verificarSesion();
    });
  }

  verificarSesion(): void {
    // Si existe el ID en localStorage, la variable se vuelve true
    this.sesionIniciada = localStorage.getItem('idUsuarioActual') !== null;
  }
}
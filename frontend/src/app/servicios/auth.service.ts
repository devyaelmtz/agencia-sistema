import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
export interface Perfil { id: number; nombreUsuario: string; correo: string; telefono: string; }
export interface Registro { nombreUsuario: string; correo: string; telefono: string; password: string; }
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  register(datos: Registro) { return this.http.post<Perfil>('/api/auth/register', datos); }
  login(datos: {correo: string; password: string}) { return this.http.post<Perfil>('/api/auth/login', datos); }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
export interface Perfil { id: number; nombreUsuario: string; correo: string; telefono: string; }
export interface Registro { nombreUsuario: string; correo: string; telefono: string; password: string; }
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  register(datos: Registro) { return this.http.post<Perfil>('/api/auth/register', datos); }
  private readonly key = 'agencia_access_token';
  get token(): string | null {
    const token = sessionStorage.getItem(this.key);
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      if (typeof payload.exp === 'number' && payload.exp * 1000 > Date.now()) return token;
    } catch { /* Un token ilegible no constituye una sesión. */ }
    this.logout();
    return null;
  }
  logout() { sessionStorage.removeItem(this.key); localStorage.removeItem('idUsuarioActual'); }
  me() { return this.http.get<Perfil>('/api/auth/me'); }
  login(datos: {correo: string; password: string}) { return this.http.post<{accessToken: string; usuario: Perfil}>('/api/auth/login', datos).pipe(tap(res => sessionStorage.setItem(this.key, res.accessToken))); }
}

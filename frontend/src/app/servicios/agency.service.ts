import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
export interface Client {
  id: number;
  version: number;
  usuarioId: number;
  nombre: string;
  apellido: string;
  rfc: string;
  direccion: string;
  telefono: string;
  correo: string;
  recibirPublicidad: boolean;
}
export interface User {
  id: number;
  version: number;
  nombreUsuario: string;
  correo: string;
  telefono: string;
  rol: 'ROLE_ADMIN' | 'ROLE_USER';
}
export interface Sale {
  id: number;
  version: number;
  clienteId: number;
  vehiculoId: number;
  usuarioId: number;
  fecha: string;
  montoTotal: number;
  metodoPago: string;
}
export interface Approval {
  id: number;
  version: number;
  usuarioId: number;
  tipo: string;
  estado: string;
  descripcion: string;
  tablaAfectada?: string;
  registroId?: number;
  registroVersion?: number;
  propuesta?: string;
  resultado?: string;
  administradorId?: number;
  fechaResolucion?: string;
  motivoResolucion?: string;
  createdAt: string;
}
export interface Notice {
  descripcion: string;
  avisoLeido: boolean;
  avisoVersion: string;
}
export const policyVersion = 'academico-1';
@Injectable({ providedIn: 'root' })
export class ClientService {
  private http = inject(HttpClient);
  link(id: number, version: number, usuarioId: number) {
    return this.http.put<Client>('/api/admin/clientes/' + id + '/owner', { version, usuarioId });
  }
  list() {
    return this.http.get<Client[]>('/api/clientes');
  }
  create(datos: unknown) {
    return this.http.post<Client>('/api/clientes', datos);
  }
  update(id: number, version: number, datos: unknown, notice: Notice) {
    return this.http.put<Client | Approval>('/api/clientes/' + id, { version, datos, ...notice });
  }
  delete(id: number, version: number, notice: Notice) {
    return this.http.delete<Approval | null>('/api/clientes/' + id, {
      params: { version, ...notice },
    });
  }
}
@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  list() {
    return this.http.get<User[]>('/api/usuarios');
  }
  get(id: number) {
    return this.http.get<User>('/api/usuarios/' + id);
  }
  create(datos: unknown) {
    return this.http.post<User>('/api/usuarios', datos);
  }
  update(id: number, version: number, datos: unknown, notice: Notice) {
    return this.http.put<User | Approval>('/api/usuarios/' + id, { version, datos, ...notice });
  }
  delete(id: number, version: number, notice: Notice) {
    return this.http.delete<Approval | null>('/api/usuarios/' + id, {
      params: { version, ...notice },
    });
  }
}
@Injectable({ providedIn: 'root' })
export class SaleService {
  private http = inject(HttpClient);
  list() {
    return this.http.get<Sale[]>('/api/ventas');
  }
  create(datos: unknown) {
    return this.http.post<Sale>('/api/ventas', datos);
  }
  update(id: number, version: number, datos: unknown) {
    return this.http.put<Sale>('/api/ventas/' + id, { version, datos });
  }
  delete(id: number, version: number) {
    return this.http.delete<void>('/api/ventas/' + id, { params: { version } });
  }
}
@Injectable({ providedIn: 'root' })
export class ApprovalService {
  private http = inject(HttpClient);
  list(admin = false) {
    return this.http.get<Approval[]>(admin ? '/api/admin/arco-requests' : '/api/arco/my-requests');
  }
  request(data: unknown) {
    return this.http.post<Approval>('/api/arco-requests/actions', data);
  }
  edit(id: number, version: number, descripcion: string) {
    return this.http.put<Approval>('/api/arco-requests/' + id, { version, descripcion });
  }
  withdraw(id: number, version: number) {
    return this.http.delete<Approval>('/api/arco-requests/' + id, { params: { version } });
  }
  resolve(id: number, decision: 'APROBAR' | 'RECHAZAR', motivo: string) {
    return this.http.post<Approval>('/api/admin/arco-requests/' + id + '/resolve', {
      decision,
      motivo,
    });
  }
}

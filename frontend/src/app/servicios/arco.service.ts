import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
export interface ArcoRequest {
  id: number;
  version?: number;
  tipo: string;
  descripcion: string;
  estado: string;
  evidencia: string;
  createdAt: string;
  tablaAfectada?: string;
  registroId?: number;
  propuesta?: string;
  resultado?: string;
  motivoResolucion?: string;
  administradorId?: number;
  fechaResolucion?: string;
}
@Injectable({ providedIn: 'root' })
export class ArcoService {
  private http = inject(HttpClient);
  list() {
    return this.http.get<ArcoRequest[]>('/api/arco-requests');
  }
  create(data: {
    tipo: string;
    descripcion: string;
    evidencia: string;
    avisoLeido: boolean;
    avisoVersion: string;
  }) {
    return this.http.post<ArcoRequest>('/api/arco-requests', data);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
export interface Vehicle { id?: number; marca: string; modelo: string; anio: number; vin: string; precio: number; kilometraje: number; estado: 'DISPONIBLE'|'VENDIDO'|'MANTENIMIENTO'; }
@Injectable({providedIn: 'root'})
export class VehicleService {
  private http = inject(HttpClient);
  list() { return this.http.get<Vehicle[]>('/api/vehicles'); }
  save(v: Vehicle) { return v.id ? this.http.put<Vehicle>('/api/vehicles/'+v.id,v) : this.http.post<Vehicle>('/api/vehicles',v); }
  delete(id: number) { return this.http.delete<void>('/api/vehicles/'+id); }
}

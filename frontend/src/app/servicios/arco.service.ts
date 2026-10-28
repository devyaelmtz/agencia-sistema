import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
export interface ArcoRequest { id:number; tipo:string; descripcion:string; estado:string; evidencia:string; createdAt:string; }
@Injectable({providedIn:'root'})
export class ArcoService {
 private http=inject(HttpClient);
 list() {return this.http.get<ArcoRequest[]>('/api/arco-requests');}
 create(data:{tipo:string;descripcion:string;evidencia:string;avisoLeido:boolean;avisoVersion:string}) {return this.http.post<ArcoRequest>('/api/arco-requests',data);}
}

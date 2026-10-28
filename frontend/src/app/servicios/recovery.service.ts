import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable({providedIn:'root'})
export class RecoveryService {
 private http=inject(HttpClient);
 forgot(correo:string) {return this.http.post<{message:string}>('/api/auth/forgot-password',{correo});}
 reset(token:string,password:string) {return this.http.post<{message:string}>('/api/auth/reset-password',{token,password});}
}

import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ArcoService, ArcoRequest } from '../../servicios/arco.service';
@Component({selector:'app-arco-request',standalone:true,imports:[CommonModule,ReactiveFormsModule,RouterLink],templateUrl:'./arco-request.component.html',styles:[`:host{display:block;max-width:850px;margin:auto}.auth-card{max-width:none}.notice{padding:20px;background:#eef3fb;border:1px solid #b6c8e6;border-radius:8px;margin:20px 0}textarea,select{font:inherit;width:100%;padding:12px;border:1px solid #94a3b8;border-radius:6px}.check{display:flex;align-items:flex-start;gap:12px}.check input{margin-top:5px}article{border-top:1px solid #dce2eb;padding:16px 0;overflow-wrap:anywhere}small{display:block}`]})
export class ArcoRequestComponent implements OnInit {
 private api=inject(ArcoService);
 requests=signal<ArcoRequest[]>([]);busy=signal(false);loading=signal(false);error=signal('');message=signal('');
 form=inject(FormBuilder).nonNullable.group({tipo:['ACCESO',Validators.required],descripcion:['',[Validators.required,Validators.minLength(10),Validators.maxLength(4000),Validators.pattern(/.*\S.*/s)]],evidencia:['',Validators.maxLength(1000)],avisoLeido:[false,Validators.requiredTrue]});
 ngOnInit() {this.load();}
 load() {this.loading.set(true);this.api.list().pipe(finalize(()=>this.loading.set(false))).subscribe({next:r=>this.requests.set(r),error:()=>this.error.set('No fue posible cargar tus solicitudes.')});}
 enviar() {
  this.form.markAllAsTouched();if(this.form.invalid || this.busy())return;
  this.busy.set(true);this.error.set('');this.message.set('');
  this.api.create({...this.form.getRawValue(),avisoVersion:'academico-1'}).pipe(finalize(()=>this.busy.set(false))).subscribe({next:r=>{this.requests.update(rows=>[r,...rows]);this.form.reset();this.message.set('Solicitud enviada. Conserva el folio '+r.id+' para seguimiento.');},error:e=>this.error.set(e.error?.message || 'No fue posible enviar la solicitud.')});
 }
}

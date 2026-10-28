import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Vehicle, VehicleService } from '../../servicios/vehicle.service';
@Component({selector: 'app-admin-vehicles', standalone: true, imports: [CommonModule,ReactiveFormsModule,RouterLink], templateUrl: './admin-vehicles.component.html', styleUrl: './admin-vehicles.component.css'})
export class AdminVehiclesComponent implements OnInit {
  private api = inject(VehicleService);
  vehicles: Vehicle[] = []; loading = false; busy = false; editing = false; id?: number;
  pendingDelete?: Vehicle; message = ''; error = '';
  maxYear = new Date().getFullYear()+1;
  form = inject(FormBuilder).nonNullable.group({
    marca: ['', [Validators.required, Validators.maxLength(100), Validators.pattern(/.*\S.*/)]],
    modelo: ['', [Validators.required, Validators.maxLength(100), Validators.pattern(/.*\S.*/)]],
    anio: [new Date().getFullYear(), [Validators.required,Validators.min(1886),Validators.max(this.maxYear),Validators.pattern(/^\d+$/)]],
    vin: ['', [Validators.required,Validators.pattern(/^[A-HJ-NPR-Z0-9]{17}$/)]],
    precio: [0, [Validators.required,Validators.min(0),Validators.max(9999999999999.99),Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
    kilometraje: [0, [Validators.required,Validators.min(0),Validators.pattern(/^\d+$/)]],
    estado: ['DISPONIBLE' as Vehicle['estado'], Validators.required]
  });
  ngOnInit() { this.load(); }
  load() { this.loading = true; this.api.list().pipe(finalize(() => this.loading=false)).subscribe({next: rows => this.vehicles=rows,error: () => this.error='No se pudo cargar el inventario. Intenta de nuevo.'}); }
  open(v?: Vehicle) {
    this.error=''; this.message=''; this.pendingDelete=undefined; this.id=v?.id;
    this.form.reset(v ?? {marca:'',modelo:'',anio:new Date().getFullYear(),vin:'',precio:0,kilometraje:0,estado:'DISPONIBLE'});
    this.editing=true;
    setTimeout(() => document.getElementById('marca')?.focus());
  }
  save() {
    this.form.markAllAsTouched(); if (this.form.invalid || this.busy) return;
    this.busy=true; this.error='';
    this.api.save({...this.form.getRawValue(),id:this.id}).pipe(finalize(() => this.busy=false)).subscribe({next: () => {this.editing=false;this.message='Vehículo guardado correctamente.';this.load();},error: e => this.error=e.status===409?'Este VIN ya está registrado.': 'No se pudo guardar. Revisa los datos e intenta de nuevo.'});
  }
  remove() {
    if (!this.pendingDelete?.id || this.busy) return;
    this.busy=true;this.error='';
    this.api.delete(this.pendingDelete.id).pipe(finalize(() => this.busy=false)).subscribe({next: () => {this.pendingDelete=undefined;this.message='Vehículo eliminado.';this.load();},error: () => this.error='No se pudo eliminar el vehículo. Intenta de nuevo.'});
  }
}

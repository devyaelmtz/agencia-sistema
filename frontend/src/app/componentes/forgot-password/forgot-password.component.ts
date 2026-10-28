import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { RecoveryService } from '../../servicios/recovery.service';
@Component({selector:'app-forgot-password',standalone:true,imports:[CommonModule,ReactiveFormsModule,RouterLink],templateUrl:'./forgot-password.component.html'})
export class ForgotPasswordComponent {
 private api=inject(RecoveryService);
 form=inject(FormBuilder).nonNullable.group({correo: ['',[Validators.required,Validators.email,Validators.maxLength(100)]]});
 busy=signal(false); message=signal(''); error=signal('');
 enviar() {
  this.form.markAllAsTouched();if(this.form.invalid || this.busy()) return;
  this.busy.set(true);this.error.set('');this.message.set('');
  this.api.forgot(this.form.getRawValue().correo).pipe(finalize(()=>this.busy.set(false))).subscribe({next:r=>{this.message.set(r.message);this.form.reset();},error:e=>this.error.set(e.error?.message || 'No fue posible completar la operación. Intenta de nuevo.')});
 }
}

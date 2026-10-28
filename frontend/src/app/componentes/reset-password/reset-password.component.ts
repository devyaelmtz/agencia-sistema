import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { RecoveryService } from '../../servicios/recovery.service';
@Component({selector:'app-reset-password',standalone:true,imports:[CommonModule,ReactiveFormsModule,RouterLink],templateUrl:'./reset-password.component.html'})
export class ResetPasswordComponent {
 private api=inject(RecoveryService);
 form=inject(FormBuilder).nonNullable.group({token: [inject(ActivatedRoute).snapshot.queryParamMap.get('token') ?? '',[Validators.required,Validators.pattern(/^[A-Za-z0-9_-]{43}$/)]], password: ['',[Validators.required,Validators.minLength(8),Validators.maxLength(64)]]});
 busy=signal(false); message=signal(''); error=signal('');
 enviar() {
  this.form.markAllAsTouched();if(this.form.invalid || this.busy()) return;
  this.busy.set(true);this.error.set('');this.message.set('');
  this.api.reset(this.form.getRawValue().token,this.form.getRawValue().password).pipe(finalize(()=>this.busy.set(false))).subscribe({next:r=>{this.message.set(r.message);this.form.reset();},error:e=>this.error.set(e.error?.message || 'No fue posible completar la operación. Intenta de nuevo.')});
 }
}

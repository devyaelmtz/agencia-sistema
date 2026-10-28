import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../servicios/auth.service';
@Component({ selector: 'app-login', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink], templateUrl: './login.component.html' })
export class LoginComponent {
  private auth = inject(AuthService);
  form = inject(FormBuilder).nonNullable.group({

    correo: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]]
  });
  pendiente = false;
  error = '';
  mensaje = '';
  enviar() {
    if (this.pendiente) return;
    this.form.markAllAsTouched();
    this.error = ''; this.mensaje = '';
    if (this.form.invalid) return;
    this.pendiente = true;
    this.auth.login(this.form.getRawValue()).pipe(finalize(() => this.pendiente = false)).subscribe({
      next: usuario => { this.mensaje = 'Bienvenido, ' + usuario.nombreUsuario + '. Acceso validado.'; this.form.reset(); },
      error: err => this.error = err.error?.message || 'No fue posible conectar con el servidor. Intenta de nuevo.'
    });
  }
}

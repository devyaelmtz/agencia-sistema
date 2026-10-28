import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../servicios/auth.service';
@Component({ selector: 'app-register', standalone: true, imports: [CommonModule, ReactiveFormsModule, RouterLink], templateUrl: './register.component.html' })
export class RegisterComponent {
  private auth = inject(AuthService);
  form = inject(FormBuilder).nonNullable.group({
    nombreUsuario: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern(/.*\S.*/)]], telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
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
    this.auth.register(this.form.getRawValue()).pipe(finalize(() => this.pendiente = false)).subscribe({
      next: usuario => { this.mensaje = 'Registro completado. Ya puedes iniciar sesión.'; this.form.reset(); },
      error: err => this.error = err.error?.message || 'No fue posible conectar con el servidor. Intenta de nuevo.'
    });
  }
}

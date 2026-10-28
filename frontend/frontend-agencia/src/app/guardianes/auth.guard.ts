import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  // Si hay un ID en localStorage, lo dejamos pasar
  if (localStorage.getItem('idUsuarioActual')) {
    return true;
  }

  // Si no está logueado, lo pateamos de vuelta al Login y bloqueamos el paso
  router.navigate(['/login']);
  return false;
};
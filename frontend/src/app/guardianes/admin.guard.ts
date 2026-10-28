import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../servicios/auth.service';
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService), router = inject(Router);
  return auth.token ? (auth.isAdmin || router.createUrlTree(['/dashboard'])) : router.createUrlTree(['/login']);
};

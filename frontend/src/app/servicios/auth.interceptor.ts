import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const url = new URL(req.url, window.location.origin);
  const api = (url.origin === window.location.origin || url.origin === 'http://localhost:8080') && url.pathname.startsWith('/api/');
  const publicEndpoint = ['/api/auth/login', '/api/auth/register'].includes(url.pathname);
  const token = api && !publicEndpoint ? auth.token : null;
  return next(token ? req.clone({setHeaders: {Authorization: `Bearer ${token}`}}) : req).pipe(catchError(err => {
    if (api && !publicEndpoint && err instanceof HttpErrorResponse && err.status === 401) {
      auth.logout(); void router.navigate(['/login']);
    }
    return throwError(() => err);
  }));
};

import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

// 1. NUEVO: Importa el proveedor de HTTP
import { authInterceptor } from './servicios/auth.interceptor';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // 2. NUEVO: Agrega el proveedor a la lista
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};

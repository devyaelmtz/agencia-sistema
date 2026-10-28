import { Routes } from '@angular/router';
import { VehiculosComponent } from './componentes/vehiculos/vehiculos.component';
import { ClientesComponent } from './componentes/clientes/clientes.component';
import { ArcoComponent } from './componentes/arco/arco.component';
import { LoginComponent } from './componentes/login/login.component';
import { authGuard } from './guardianes/auth.guard'; // <-- Importamos el guardián

export const routes: Routes = [
  // A estas 3 rutas les ponemos el candado:
  { path: 'vehiculos', component: VehiculosComponent, canActivate: [authGuard] },
  { path: 'clientes', component: ClientesComponent, canActivate: [authGuard] },
  { path: 'arco', component: ArcoComponent, canActivate: [authGuard] },

  // El login NO lleva candado, porque es la entrada libre
  { path: 'login', component: LoginComponent },

  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
import { Routes } from '@angular/router';
import { LoginComponent } from './componentes/login/login.component';
import { RegisterComponent } from './componentes/register/register.component';
import { DashboardComponent } from './componentes/dashboard/dashboard.component';
import { authGuard } from './guardianes/auth.guard';
import { AdminVehiclesComponent } from './componentes/admin-vehicles/admin-vehicles.component';
import { adminGuard } from './guardianes/admin.guard';
import { ForgotPasswordComponent } from './componentes/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './componentes/reset-password/reset-password.component';
import { ArcoRequestComponent } from './componentes/arco-request/arco-request.component';
import { AgencyCrudComponent } from './componentes/agency-crud/agency-crud.component';
import { AdminArcoComponent } from './componentes/admin-arco/admin-arco.component';
export const routes: Routes = [
  {
    path: 'clientes',
    component: AgencyCrudComponent,
    canActivate: [authGuard],
    data: { kind: 'clientes' },
  },
  {
    path: 'perfil',
    component: AgencyCrudComponent,
    canActivate: [authGuard],
    data: { kind: 'perfil' },
  },
  {
    path: 'admin/usuarios',
    component: AgencyCrudComponent,
    canActivate: [adminGuard],
    data: { kind: 'usuarios' },
  },
  {
    path: 'admin/ventas',
    component: AgencyCrudComponent,
    canActivate: [adminGuard],
    data: { kind: 'ventas' },
  },
  { path: 'admin/arco', component: AdminArcoComponent, canActivate: [adminGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'arco', component: ArcoRequestComponent, canActivate: [authGuard] },
  { path: 'admin/vehicles', component: AdminVehiclesComponent, canActivate: [adminGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];

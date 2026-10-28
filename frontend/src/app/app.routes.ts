import { Routes } from '@angular/router';
import { LoginComponent } from './componentes/login/login.component';
import { RegisterComponent } from './componentes/register/register.component';
import { DashboardComponent } from './componentes/dashboard/dashboard.component';
import { authGuard } from './guardianes/auth.guard';
import { AdminVehiclesComponent } from './componentes/admin-vehicles/admin-vehicles.component';
import { adminGuard } from './guardianes/admin.guard';
export const routes: Routes = [
  { path: 'admin/vehicles', component: AdminVehiclesComponent, canActivate: [adminGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

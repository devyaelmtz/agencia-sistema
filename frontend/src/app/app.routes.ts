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
export const routes: Routes = [
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'arco', component: ArcoRequestComponent, canActivate: [authGuard] },
  { path: 'admin/vehicles', component: AdminVehiclesComponent, canActivate: [adminGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];

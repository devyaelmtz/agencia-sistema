import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ResetPasswordComponent } from './reset-password.component';
describe('ResetPasswordComponent',()=>{
 beforeEach(()=>TestBed.configureTestingModule({providers:[provideRouter([]),provideHttpClient(),provideHttpClientTesting()]}));
 afterEach(()=>{try{TestBed.inject(HttpTestingController).verify();}finally{TestBed.resetTestingModule();}});
 it('submits the code and new password and shows expired-code feedback',()=>{
  const c=TestBed.createComponent(ResetPasswordComponent).componentInstance;
  c.form.setValue({token:'A'.repeat(43),password:'NuevaClave123'});c.enviar();
  const req=TestBed.inject(HttpTestingController).expectOne('/api/auth/reset-password');
  expect(req.request.body.password).toBe('NuevaClave123');
  req.flush({message:'Código inválido o caducado.'},{status:400,statusText:'Bad Request'});
  expect(c.busy()).toBe(false);expect(c.error()).toBe('Código inválido o caducado.');
 });
});

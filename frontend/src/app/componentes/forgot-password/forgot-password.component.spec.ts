import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ForgotPasswordComponent } from './forgot-password.component';
describe('ForgotPasswordComponent',()=>{
 beforeEach(()=>TestBed.configureTestingModule({providers:[provideRouter([]),provideHttpClient(),provideHttpClientTesting()]}));
 afterEach(()=>{try{TestBed.inject(HttpTestingController).verify();}finally{TestBed.resetTestingModule();}});
 it('rejects invalid email and handles successful recovery request',()=>{
  const c=TestBed.createComponent(ForgotPasswordComponent).componentInstance;const http=TestBed.inject(HttpTestingController);
  c.enviar();http.expectNone('/api/auth/forgot-password');
  c.form.controls.correo.setValue('test@example.invalid');c.enviar();
  http.expectOne('/api/auth/forgot-password').flush({message:'Revisa tu correo'});
  expect(c.message()).toBe('Revisa tu correo');expect(c.busy()).toBe(false);
 });
 it('unlocks after connection failure',()=>{
  const c=TestBed.createComponent(ForgotPasswordComponent).componentInstance;c.form.controls.correo.setValue('test@example.invalid');c.enviar();
  TestBed.inject(HttpTestingController).expectOne('/api/auth/forgot-password').flush({}, {status:500,statusText:'Error'});
  expect(c.busy()).toBe(false);expect(c.error()).not.toBe('');
 });
});

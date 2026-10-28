import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { RegisterComponent } from './register.component';
describe('RegisterComponent', () => {
  beforeEach(() => TestBed.configureTestingModule({providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]}));
  it('requires a phone of exactly ten digits', () => {
    const component = TestBed.createComponent(RegisterComponent).componentInstance;
    for (const value of ['', '123', '12345678901', 'abcdefghij']) {
      component.form.controls.telefono.setValue(value);
      expect(component.form.controls.telefono.invalid).toBe(true);
    }
    component.form.controls.telefono.setValue('5512345678');
    expect(component.form.controls.telefono.valid).toBe(true);
  });
  it('submits valid registration to the API and shows success', () => {
    const component = TestBed.createComponent(RegisterComponent).componentInstance;
    component.form.setValue({nombreUsuario: 'Prueba', correo: 'test@example.invalid', telefono: '5512345678', password: 'ClaveSegura123'});
    component.enviar();
    const http = TestBed.inject(HttpTestingController);
    const req = http.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body.telefono).toBe('5512345678');
    req.flush({id: 1, nombreUsuario: 'Prueba'});
    expect(component.mensaje).toContain('Registro completado');
    expect(component.pendiente).toBe(false);
    http.verify();
  });
});

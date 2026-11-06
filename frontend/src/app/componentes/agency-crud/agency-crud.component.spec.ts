import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from '../../servicios/auth.service';
import { AgencyCrudComponent } from './agency-crud.component';
const client = {
  id: 12,
  version: 3,
  usuarioId: 2,
  nombre: 'Original',
  apellido: 'Prueba',
  rfc: 'XAXX010101000',
  direccion: 'Calle Prueba 123',
  telefono: '5551234567',
  correo: 'cliente@example.invalid',
  recibirPublicidad: true,
};
describe('Client changes pending ARCO approval', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: { snapshot: { data: { kind: 'clientes' } } } },
        { provide: AuthService, useValue: { isAdmin: false } },
      ],
    }),
  );
  afterEach(() => {
    try {
      TestBed.inject(HttpTestingController).verify();
    } finally {
      TestBed.resetTestingModule();
    }
  });
  it('keeps the original visible and identifies a 202 response as pending', () => {
    const f = TestBed.createComponent(AgencyCrudComponent);
    f.detectChanges();
    const http = TestBed.inject(HttpTestingController);
    http.expectOne('/api/clientes').flush([client]);
    const c = f.componentInstance;
    c.open(client);
    c.data['nombre'] = 'Propuesto';
    c.description = 'Solicito corregir mi nombre';
    c.notice = true;
    c.save();
    const request = http.expectOne('/api/clientes/12');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body.version).toBe(3);
    expect(request.request.body.datos.nombre).toBe('Propuesto');
    expect(request.request.body.avisoVersion).toBe('academico-1');
    request.flush({ id: 41, estado: 'PENDIENTE' }, { status: 202, statusText: 'Accepted' });
    http.expectOne('/api/clientes').flush([client]);
    f.detectChanges();
    expect(c.rows()[0]).toEqual(client);
    expect(f.nativeElement.textContent).toContain('El registro original no ha cambiado');
    expect(c.busy()).toBe(false);
  });
  it('does not submit without notice and preserves the edit after a conflict', () => {
    const f = TestBed.createComponent(AgencyCrudComponent);
    f.detectChanges();
    const http = TestBed.inject(HttpTestingController);
    http.expectOne('/api/clientes').flush([client]);
    const c = f.componentInstance;
    c.open(client);
    c.save();
    http.expectNone((r) => r.method === 'PUT');
    c.notice = true;
    c.description = 'Solicito corregir mi nombre';
    c.save();
    http.expectOne('/api/clientes/12').flush({}, { status: 409, statusText: 'Conflict' });
    expect(c.editing()).toBe(true);
    expect(c.error()).toContain('El registro cambió');
    expect(c.busy()).toBe(false);
  });
});

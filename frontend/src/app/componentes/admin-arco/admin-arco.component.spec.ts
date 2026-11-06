import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AdminArcoComponent } from './admin-arco.component';
const request = {
  id: 31,
  version: 0,
  usuarioId: 5,
  tipo: 'RECTIFICACION',
  estado: 'PENDIENTE',
  descripcion: 'Corregir mis datos',
  tablaAfectada: 'clientes',
  registroId: 12,
  registroVersion: 3,
  createdAt: '2026-11-06T00:00:00Z',
};
describe('Administrative ARCO resolution', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }),
  );
  afterEach(() => {
    try {
      TestBed.inject(HttpTestingController).verify();
    } finally {
      TestBed.resetTestingModule();
    }
  });
  it('requires a reason and preserves pending state when approval conflicts', () => {
    const f = TestBed.createComponent(AdminArcoComponent);
    f.detectChanges();
    const http = TestBed.inject(HttpTestingController);
    http.expectOne('/api/admin/arco-requests').flush([request]);
    const c = f.componentInstance;
    c.open(request);
    c.resolve();
    http.expectNone((r) => r.method === 'POST');
    c.reason = 'Solicitud revisada';
    c.resolve();
    const r = http.expectOne('/api/admin/arco-requests/31/resolve');
    expect(r.request.body).toEqual({ decision: 'APROBAR', motivo: 'Solicitud revisada' });
    r.flush({}, { status: 409, statusText: 'Conflict' });
    expect(c.selected()?.estado).toBe('PENDIENTE');
    expect(c.error()).toContain('Conflicto');
    expect(c.busy()).toBe(false);
  });
  it('sends rejection explicitly and refreshes the resolved list', () => {
    const f = TestBed.createComponent(AdminArcoComponent);
    f.detectChanges();
    const http = TestBed.inject(HttpTestingController);
    http.expectOne('/api/admin/arco-requests').flush([request]);
    const c = f.componentInstance;
    c.open(request);
    c.decision = 'RECHAZAR';
    c.reason = 'No procede la solicitud';
    c.resolve();
    const r = http.expectOne('/api/admin/arco-requests/31/resolve');
    expect(r.request.body.decision).toBe('RECHAZAR');
    r.flush({ ...request, estado: 'RECHAZADA' });
    http.expectOne('/api/admin/arco-requests').flush([{ ...request, estado: 'RECHAZADA' }]);
    expect(c.selected()).toBeNull();
    expect(c.rows()[0].estado).toBe('RECHAZADA');
  });
});

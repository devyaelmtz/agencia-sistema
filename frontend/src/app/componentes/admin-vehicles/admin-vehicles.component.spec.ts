import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AdminVehiclesComponent } from './admin-vehicles.component';
import { Vehicle } from '../../servicios/vehicle.service';

describe('AdminVehiclesComponent saving', () => {
  let fixture: ComponentFixture<AdminVehiclesComponent>;
  let http: HttpTestingController;
  const vehicle: Vehicle = {id: 1, marca: 'Toyota', modelo: 'Corolla', anio: 2024, vin: '1HGCM82633A004352', precio: 250000, kilometraje: 1000, estado: 'DISPONIBLE'};

  beforeEach(async () => {
    TestBed.configureTestingModule({providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]});
    fixture = TestBed.createComponent(AdminVehiclesComponent);
    http = TestBed.inject(HttpTestingController);
    fixture.autoDetectChanges();
    http.expectOne('/api/vehicles').flush([]);
    await fixture.whenStable();
  });
  afterEach(() => { try { http.verify(); } finally { TestBed.resetTestingModule(); } });

  it('renders the POST response immediately and resets and hides the form', async () => {
    const c = fixture.componentInstance;
    c.open(); c.form.patchValue(vehicle); c.save();
    fixture.changeDetectorRef.markForCheck();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(fixture.nativeElement.textContent).toContain('Guardando…');
    const req = http.expectOne('/api/vehicles');
    expect(req.request.method).toBe('POST');
    req.flush(vehicle);
    await fixture.whenStable();
    expect(c.busy).toBe(false);
    expect(c.form.controls.marca.value).toBe('');
    expect(c.editing).toBe(false);
    expect(fixture.nativeElement.querySelector('.editor')).toBeNull();
    expect(fixture.nativeElement.querySelector('tbody').textContent).toContain('Toyota Corolla');
    expect(fixture.nativeElement.textContent).not.toContain('Guardando…');
  });

  it('updates the existing row after PUT without duplicating it', async () => {
    const c = fixture.componentInstance;
    c.vehicles = [vehicle]; c.open(vehicle); c.form.controls.modelo.setValue('Yaris'); c.save();
    const req = http.expectOne('/api/vehicles/1');
    expect(req.request.method).toBe('PUT');
    req.flush({...vehicle, modelo: 'Yaris'});
    await fixture.whenStable();
    expect(c.vehicles.length).toBe(1);
    expect(fixture.nativeElement.querySelector('tbody').textContent).toContain('Toyota Yaris');
    expect(c.busy).toBe(false);
  });

  for (const status of [409, 500]) {
    it(`unlocks the form after ${status} and preserves values for retry`, async () => {
      const c = fixture.componentInstance;
      c.open(); c.form.patchValue(vehicle); c.save();
      fixture.changeDetectorRef.markForCheck();
    fixture.detectChanges();
      http.expectOne('/api/vehicles').flush({}, {status, statusText: 'Error'});
      await fixture.whenStable();
      expect(c.busy).toBe(false);
      expect(c.editing).toBe(true);
      expect(c.form.controls.marca.value).toBe('Toyota');
      expect(fixture.nativeElement.querySelector('[role=alert]').textContent).toContain(status === 409 ? 'VIN' : 'No se pudo guardar');
      const submit = fixture.nativeElement.querySelector('form button:not([type=button])');
      expect(submit.disabled).toBe(false);
      expect(submit.textContent).not.toContain('Guardando…');
    });
  }
});

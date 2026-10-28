import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { ArcoRequestComponent } from './arco-request.component';
describe('ARCO notice and submission',()=>{
 beforeEach(()=>TestBed.configureTestingModule({providers:[provideRouter([]),provideHttpClient(),provideHttpClientTesting()]}));
 afterEach(()=>{try{TestBed.inject(HttpTestingController).verify();}finally{TestBed.resetTestingModule();}});
 it('shows the notice and prevents submission without confirming reading',()=>{
  const fixture=TestBed.createComponent(ArcoRequestComponent);fixture.detectChanges();
  const http=TestBed.inject(HttpTestingController);http.expectOne('/api/arco-requests').flush([]);
  expect(fixture.nativeElement.textContent).toContain('Políticas de Privacidad');
  fixture.componentInstance.form.controls.descripcion.setValue('Quiero consultar mis datos');
  fixture.componentInstance.enviar();http.expectNone(r=>r.method==='POST');
  expect(fixture.componentInstance.form.controls.avisoLeido.invalid).toBe(true);
 });
 it('sends notice version and renders the created request immediately',async()=>{
  const fixture=TestBed.createComponent(ArcoRequestComponent);fixture.autoDetectChanges();
  const http=TestBed.inject(HttpTestingController);http.expectOne('/api/arco-requests').flush([]);
  fixture.componentInstance.form.patchValue({descripcion:'Quiero consultar mis datos',avisoLeido:true});fixture.componentInstance.enviar();
  const req=http.expectOne('/api/arco-requests');expect(req.request.body.avisoVersion).toBe('academico-1');
  req.flush({id:42,tipo:'ACCESO',descripcion:'Quiero consultar mis datos',estado:'PENDIENTE',createdAt:new Date().toISOString()});
  await fixture.whenStable();expect(fixture.nativeElement.textContent).toContain('Folio 42');expect(fixture.componentInstance.busy()).toBe(false);
 });
});

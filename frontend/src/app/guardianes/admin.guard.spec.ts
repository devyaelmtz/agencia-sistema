import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, UrlTree } from '@angular/router';
import { adminGuard } from './admin.guard';
describe('adminGuard', () => {
  beforeEach(() => { sessionStorage.clear(); TestBed.configureTestingModule({providers:[provideHttpClient(),provideRouter([])]}); });
  afterEach(() => sessionStorage.clear());
  it('redirects missing sessions and regular users, admits admins', () => {
    const guard=()=>TestBed.runInInjectionContext(()=>adminGuard({} as never,{} as never));
    expect(guard() instanceof UrlTree).toBe(true);
    for(const role of ['ROLE_USER','ROLE_ADMIN']) {
      sessionStorage.setItem('agencia_access_token','header.'+btoa(JSON.stringify({exp:Date.now()/1000+3600,roles:[role]}))+'.signature');
      expect(guard()===true).toBe(role==='ROLE_ADMIN');
    }
  });
});

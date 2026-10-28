import { TestBed } from '@angular/core/testing';
import { provideHttpClient, withInterceptors, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { authInterceptor } from './auth.interceptor';
import { authGuard } from '../guardianes/auth.guard';
const token = (exp: number) => 'header.' + btoa(JSON.stringify({exp})) + '.signature';
describe('JWT session', () => {
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({providers: [provideRouter([]), provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting()]});
  });
  afterEach(() => { TestBed.inject(HttpTestingController).verify(); sessionStorage.clear(); });
  it('stores login token and removes it on logout', () => {
    const auth = TestBed.inject(AuthService);
    const jwt = token(Date.now() / 1000 + 3600);
    auth.login({correo: 'a@b.com', password: 'password123'}).subscribe();
    TestBed.inject(HttpTestingController).expectOne('/api/auth/login').flush({accessToken: jwt});
    expect(auth.token).toBe(jwt);
    auth.logout();
    expect(auth.token).toBeNull();
  });
  it('guards routes and rejects expired tokens', () => {
    const guard = () => TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));
    expect(guard() instanceof UrlTree).toBe(true);
    sessionStorage.setItem('agencia_access_token', token(Date.now() / 1000 + 3600));
    expect(guard()).toBe(true);
    sessionStorage.setItem('agencia_access_token', token(1));
    expect(guard() instanceof UrlTree).toBe(true);
  });
  it('attaches bearer only to application API calls', () => {
    const jwt = token(Date.now() / 1000 + 3600);
    sessionStorage.setItem('agencia_access_token', jwt);
    const http = TestBed.inject(HttpClient), mock = TestBed.inject(HttpTestingController);
    http.get('/api/auth/me').subscribe();
    const api = mock.expectOne('/api/auth/me');
    expect(api.request.headers.get('Authorization')).toBe('Bearer ' + jwt); api.flush({});
    http.get('https://example.invalid/api/data').subscribe();
    const external = mock.expectOne('https://example.invalid/api/data');
    expect(external.request.headers.has('Authorization')).toBe(false); external.flush({});
  });
  it('clears local session on a protected API 401', () => {
    sessionStorage.setItem('agencia_access_token', token(Date.now() / 1000 + 3600));
    const router = TestBed.inject(Router);
    const navigate = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    TestBed.inject(HttpClient).get('/api/auth/me').subscribe({error: () => {}});
    TestBed.inject(HttpTestingController).expectOne('/api/auth/me').flush({}, {status: 401, statusText: 'Unauthorized'});
    expect(TestBed.inject(AuthService).token).toBeNull();
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });
});

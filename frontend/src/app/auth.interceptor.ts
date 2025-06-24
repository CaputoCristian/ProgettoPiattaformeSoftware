import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthService} from './services/auth.service';
import {OAuthService} from 'angular-oauth2-oidc';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private auth: OAuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // esempio: aggiunta token (da adattare al tuo servizio Keycloak)
    const authToken = this.auth.getAuthorizationToken();;
    const authReq = req.clone({
      setHeaders: { Authorization: authToken }
    });
    return next.handle(authReq);
  }
}

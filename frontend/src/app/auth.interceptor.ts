import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // esempio: aggiunta token (da adattare al tuo servizio Keycloak)
    const authToken = 'Bearer token-di-esempio';
    const authReq = req.clone({
      setHeaders: { Authorization: authToken }
    });
    return next.handle(authReq);
  }
}

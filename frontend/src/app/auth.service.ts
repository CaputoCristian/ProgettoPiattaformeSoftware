import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private tokenUrl = 'http://localhost:8080/realms/myrealm/protocol/openid-connect/token';
  private clientId = 'angular-client';
  private clientSecret = 'PlweYNVblkXmyNR2KGLQNfaAyvO570No'; // opzionale

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('client_id', this.clientId)
      .set('username', email)
      .set('password', password)
      .set('client_secret', this.clientSecret); // rimuovi se non serve

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
    });

    return this.http.post(this.tokenUrl, body.toString(), { headers });
  }
}

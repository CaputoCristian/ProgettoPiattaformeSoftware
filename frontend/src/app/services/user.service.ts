import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { Observable } from 'rxjs';

export interface UserProfileDTO {
    firstName: string,
    lastName: string,
    email: string,
    address: string,
    telephoneNumber: string,
    birthDate: string,
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:8081'; //TODO controlla backend

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getUser(): Observable<UserProfileDTO> {
    return this.httpClient.get<UserProfileDTO>(`${this.baseUrl}/profile`, { headers: this.getHeaders() });
  }

}

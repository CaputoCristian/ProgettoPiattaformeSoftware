import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {OAuthService} from 'angular-oauth2-oidc';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private oauthService: OAuthService) {}

  login(): void {
    this.oauthService.initLoginFlow();
  }

  logout() {
    console.log('Logout cliccato');
    this.oauthService.logOut({
      postLogoutRedirectUri: window.location.origin
    });
  }

  isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  getToken(): string | null {
    return this.oauthService.getAccessToken();
  }
}

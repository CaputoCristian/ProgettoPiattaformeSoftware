import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product';
import {OAuthService} from 'angular-oauth2-oidc';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:8081';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) {
  }

  getAllProducts(): Observable<Product[]> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get<Product[]>(`${this.baseUrl}/products`, {headers});
  }

  getProductById(id: number): Observable<Product> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get<Product>(`${this.baseUrl}/${id}`, {headers});
  }

}

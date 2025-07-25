import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product';
import {OAuthService} from 'angular-oauth2-oidc';

interface ProductPurchasedDTO {
  name: string;
  quantity: number;
}

interface PurchaseDTO {
  id: number;
  date: string;
  totalPrice: number;
  products: ProductPurchasedDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private baseUrl = 'http://localhost:8081';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) {
  }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getAllPurchase(): Observable<PurchaseDTO[]> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.httpClient.get<PurchaseDTO[]>(`${this.baseUrl}/purchases/getAll`, {headers});
  }

  getPurchaseById(id: number): Observable<Product> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get<Product>(`${this.baseUrl}/purchases/${id}`, {headers});
  }

}

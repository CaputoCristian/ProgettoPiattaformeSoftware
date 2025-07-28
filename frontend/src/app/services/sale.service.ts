import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/product';
import {OAuthService} from 'angular-oauth2-oidc';

interface ProductSoldDTO {
  name: string;
  price: number;
  quantity: number;
}

interface SaleDTO {
  id: number;
  date: string;
  totalPrice: number;
  products: ProductSoldDTO[];
  shippingAddress: string;
  check: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private baseUrl = 'http://localhost:8081';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) {
  }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getAllSales(): Observable<SaleDTO[]> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.httpClient.get<SaleDTO[]>(`${this.baseUrl}/sales/getAll`, {headers});
  }

  getSaleById(id: number): Observable<Product> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get<Product>(`${this.baseUrl}/sales/${id}`, {headers});

  }

  updateSale(sale: SaleDTO): Observable<SaleDTO> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.put<SaleDTO>(`${this.baseUrl}/sales/${sale.id}/check`, sale, {headers});
  }

  checkSale(id: number): Observable<Product> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.post<Product>(`${this.baseUrl}/sales/${id}/check`, {headers});

  }

  checkNotifications(): Observable<boolean> {
  const token = this.oauthService.getAccessToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
  return this.httpClient.get<boolean>(`${this.baseUrl}/sales/hasNotifications`, {headers});}

}

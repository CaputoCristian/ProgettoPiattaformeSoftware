import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {OAuthService} from 'angular-oauth2-oidc';
import {Observable} from 'rxjs';
import {Product} from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class MarketplaceService {

  private baseUrl = 'http://localhost:8081';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) {
  }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getAllProducts(): Observable<Product[]> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get<Product[]>(`${this.baseUrl}/shop/allProducts`, {headers});
  }

  getProductById(id: number): Observable<Product> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    return this.httpClient.get<Product>(`${this.baseUrl}/${id}`, {headers});
  }

  addProduct(product: Product): Observable<Product> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.httpClient.post<Product>('http://localhost:8081/products/addProduct', product, { headers });
  }

  deleteProduct(productId: Number): Observable<any> {
    const params = { prodotto: productId.toString()};
    return this.httpClient.delete(`${this.baseUrl}/shop/delete`, { headers: this.getHeaders(), params });
  }

  editProduct(product: Product): Observable<any> {
    const params = { product: product.toString()};
    return this.httpClient.post(`${this.baseUrl}/${product.id}`, null, { headers: this.getHeaders(), params });
  }



}

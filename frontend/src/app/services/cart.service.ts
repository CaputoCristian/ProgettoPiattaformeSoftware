import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { Observable } from 'rxjs';

export interface CarrelloProdottoDTO {
  id: number;
  name: string;
  price: number;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private baseUrl = 'http://localhost:8081/cart';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getCartItems(): Observable<CarrelloProdottoDTO[]> {
    return this.httpClient.get<CarrelloProdottoDTO[]>(`${this.baseUrl}/items`, { headers: this.getHeaders() });
  }

  aggiungiAlCarrello(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString()};
    return this.httpClient.post(`${this.baseUrl}/add`, null, { headers: this.getHeaders(), params });
  }

  plusAdding(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.put(`${this.baseUrl}/plus`, null, { headers: this.getHeaders(), params });
  }

  minusRemoving(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.put(`${this.baseUrl}/minus`, null, { headers: this.getHeaders(), params });
  }

  rimuoviDalCarrello(idProdotto: number): Observable<any> {
    const url = `${this.baseUrl}/removeItem?idProdotto=${idProdotto}`;  // Includi l'idProdotto come parametro nella URL
    return this.httpClient.delete(url, { headers: this.getHeaders() });
  }

  svuotaCarrello(): Observable<any> {
    return this.httpClient.delete(`${this.baseUrl}/removeAll`, { headers: this.getHeaders() });
  }

  ordina(metodoPagamento: number, indirizzoSpedizione: string): Observable<any> {
    const params = { metodoPagamento: metodoPagamento.toString(), indirizzoSpedizione };
    return this.httpClient.post(`${this.baseUrl}/buy`, null, { headers: this.getHeaders(), params });
  }
}

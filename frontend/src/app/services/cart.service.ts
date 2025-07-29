import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import {catchError, Observable, of, retry, Subject, switchMap, tap, throwError} from 'rxjs';

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

  private cartUpdatedSource = new Subject<void>();
  cartUpdated$ = this.cartUpdatedSource.asObservable();
  private baseUrl = 'http://localhost:8081/cart';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  private refreshCart(): Observable<any> {
    return this.getCartItems().pipe(
      tap(() => this.cartUpdatedSource.next())
    );
  }

  getCartItems(): Observable<CarrelloProdottoDTO[]> {

    return this.httpClient.get<CarrelloProdottoDTO[]>(`${this.baseUrl}/items`, { headers: this.getHeaders() }
    ).pipe(
      catchError(error => {
        console.error('Errore nel recupero degli elementi del carrello:', error);
        return of([]);
      })
    );
  }


  aggiungiAlCarrello(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.post(
      `${this.baseUrl}/add`,
      null,
      { headers: this.getHeaders(), params }
    ).pipe(
      switchMap(() => this.refreshCart()),
      catchError(error => {
        console.error('Errore nell\'aggiunta al carrello:', error);
        return throwError(() => error);
      })
    );
  }


  plusAdding(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.put(
      `${this.baseUrl}/plus`,
      null,
      { headers: this.getHeaders(), params }
    ).pipe(
      switchMap(() => this.refreshCart()),
      catchError(error => {
        console.error('Errore nell\'incremento della quantità:', error);
        return throwError(() => error);
      })
    );
  }

  minusRemoving(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.put(
      `${this.baseUrl}/minus`,
      null,
      { headers: this.getHeaders(), params }
    ).pipe(
      switchMap(() => this.refreshCart()),
      catchError(error => {
        console.error('Errore nella diminuzione della quantità:', error);
        return throwError(() => error);
      })
    );
  }

  rimuoviDalCarrello(idProdotto: number): Observable<any> {
    return this.httpClient.delete(
      `${this.baseUrl}/removeItem?idProdotto=${idProdotto}`,
      {headers: this.getHeaders()}
    ).pipe(
      switchMap(() => this.refreshCart()),
      catchError(error => {
        console.error('Errore nella rimozione dal carrello:', error);
        return throwError(() => error);
      })
    );
  }

  svuotaCarrello(): Observable<any> {
    return this.httpClient.delete(
      `${this.baseUrl}/removeAll`,
      { headers: this.getHeaders() }
    ).pipe(
      switchMap(() => this.refreshCart()),
      catchError(error => {
        console.error('Errore nello svuotamento del carrello:', error);
        return throwError(() => error);
      })
    );
  }

  ordina(metodoPagamento: number, indirizzoSpedizione: string): Observable<any> {
    const params = {
      metodoPagamento: metodoPagamento.toString(),
      indirizzoSpedizione
    };
    return this.httpClient.post(
      `${this.baseUrl}/buy`,
      null,
      { headers: this.getHeaders(), params }
    ).pipe(
      switchMap(() => this.refreshCart()),
      catchError(error => {
        console.error('Errore durante l\'ordine:', error);
        return throwError(() => error);
      })
    );
  }
}


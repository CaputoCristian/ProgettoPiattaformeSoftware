import { Component, OnInit } from '@angular/core';
import {CartService, CarrelloProdottoDTO} from '../services/cart.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {OAuthEvent, OAuthService} from 'angular-oauth2-oidc';
import {filter} from 'rxjs';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CartComponent implements OnInit {

  cartItems: CarrelloProdottoDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  totalAmount: number = 0;

  // Variabili per l'ordine
  metodoPagamento: number = 1; // Ho messo giusto un valore di default cosi
  indirizzoSpedizione: string = '';

  constructor(
    private CartService: CartService,
    private oauthService: OAuthService,
  ) { }

  ngOnInit(): void { //evita di fare la richiesta prima dell'autenticazione, evita error 401
    if (this.oauthService.hasValidAccessToken()) {
      this.loadCartItems();
    } else {
      this.oauthService.events
        .pipe(filter((e: OAuthEvent) => e.type === 'token_received'))
        .subscribe(() => this.loadCartItems());
    }  }

  loadCartItems(): void {
    this.CartService.getCartItems().subscribe(
      items => {
        this.cartItems = items;
        this.calculateTotal();
        this.isLoading = false;
      },
      error => {
        console.error(error);
        this.errorMessage = 'Errore nel caricamento degli articoli del carrello.';
        this.isLoading = false;
      }
    );
  }

  calculateTotal(): void {
    this.totalAmount = this.cartItems.reduce((acc, item) => acc + (item.prezzoProdotto * item.quantita), 0);
  }

  increaseQuantity(idProdotto: number): void {
    this.CartService.plusAdding(idProdotto).subscribe(
      () => {
        const item = this.cartItems.find(item => item.idProdotto === idProdotto);
        if (item) {
          item.quantita += 1;
          this.calculateTotal();
        }
      },
      error => {
        console.error(error);
        alert('Errore durante l\'aumento della quantità.');
      }
    );
  }

  decreaseQuantity(idProdotto: number): void {
    this.CartService.minusRemoving(idProdotto).subscribe(
      () => {
        const item = this.cartItems.find(item => item.idProdotto === idProdotto);
        if (item) {
          item.quantita -= 1;
          if (item.quantita <= 0) {
            this.cartItems = this.cartItems.filter(item => item.idProdotto !== idProdotto);
          }
          this.calculateTotal();
        }
      },
      error => {
        console.error(error);
        alert('Errore durante la diminuzione della quantità.');
      }
    );
  }

  removeItem(idProdotto: number): void {
    this.isLoading = true;
    this.CartService.rimuoviDalCarrello(idProdotto).subscribe(
      () => {
        // Successo, rimuovo il prodotto dal carrello
        this.cartItems = this.cartItems.filter(item => item.idProdotto !== idProdotto);
        this.calculateTotal();
        this.isLoading = false;
      },
      (error) => {
        console.error('Errore durante la rimozione del prodotto dal carrello:', error);
        this.isLoading = false;
        alert('Errore durante la rimozione del prodotto dal carrello.');
      }
    );
  }


  emptyCart(): void {
    this.CartService.svuotaCarrello().subscribe(
      () => {
        this.cartItems = [];
        this.totalAmount = 0;
      },
      error => {
        console.error(error);
        alert('Errore durante lo svuotamento del carrello.');
      }
    );
  }

  placeOrder(): void {
    if (!this.indirizzoSpedizione) {
      alert('Per favore, inserisci un indirizzo di spedizione.');
      return;
    }

    this.CartService.ordina(this.metodoPagamento, this.indirizzoSpedizione).subscribe(
      response => {
        alert('Ordine effettuato con successo.');
        this.cartItems = [];
        this.totalAmount = 0;
        this.indirizzoSpedizione = '';
      },
      error => {
        console.error(error);
        alert('Errore durante l\'effettuazione dell\'ordine.');
      }
    );
  }
}

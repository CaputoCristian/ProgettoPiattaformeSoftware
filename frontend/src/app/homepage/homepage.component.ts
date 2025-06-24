import { Component, OnInit } from '@angular/core';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import {CommonModule, NgFor, NgIf} from '@angular/common';
import { OAuthService } from 'angular-oauth2-oidc';
import {Product} from '../models/product';
import {provideHttpClient} from '@angular/common/http';

@Component({
  selector: 'app-home',
  templateUrl: './homepage.component.html',
  imports: [CommonModule, NgFor],
  standalone: true,
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  prodotti: Product[] = [];
  quantities: number[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(private ProductService: ProductService, private CartService: CartService, private oauthService: OAuthService) { }

  ngOnInit(): void {
    if (this.oauthService.hasValidAccessToken()) {
      this.loadProducts();
    } else {

      this.oauthService.events.subscribe(event => {
        if (event.type === 'token_received') {
          this.loadProducts();
        }
      });
    }
  }

  loadProducts(): void {
    this.ProductService.getAllProducts().subscribe(
      prodotti => {
        this.prodotti = prodotti;
        this.quantities = this.prodotti.map(() => 1);
        this.isLoading = false;
      },
      error => {
        console.error('Errore nel caricamento dei prodotti:', error);
        this.errorMessage = 'Errore nel caricamento dei prodotti.';
        this.isLoading = false;
      }
    );
  }



  onQuantityChange(event: any, index: number): void {
    const quantity = event.target.value;
    if (quantity >= 1) {
      this.quantities[index] = quantity;
    }
  }

  addToCart(productId: number, quantity: number): void {
    this.CartService.aggiungiAlCarrello(productId, quantity).subscribe(
      response => {
        console.log('Successo:', response);
        alert('Prodotto aggiunto al carrello con successo.');
      },
      error => {
        console.error('Errore:', error);
        alert('Errore durante l\'aggiunta al carrello.');
      }
    );
  }
}

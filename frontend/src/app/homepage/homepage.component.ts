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
  currentPage: number = 1;
  itemsPerPage: number = 12;
  totalPages: number = 1;

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
        this.totalPages = Math.ceil(this.prodotti.length / this.itemsPerPage);
        this.currentPage = 1;
        this.isLoading = false;
      },
      error => {
        console.error('Errore nel caricamento dei prodotti:', error);
        this.errorMessage = 'Errore nel caricamento dei prodotti.';
        this.isLoading = false;
      }
    );
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  get paginatedProducts(): Product[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.prodotti.slice(start, start + this.itemsPerPage);
  }

  onQuantityChange(event: any, index: number): void {
    const quantity = event.target.value;
    if (quantity >= 1) {
      this.quantities[index] = quantity;
    }
  }

  addToCart(productId: number): void {
    this.CartService.aggiungiAlCarrello(productId).subscribe(
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

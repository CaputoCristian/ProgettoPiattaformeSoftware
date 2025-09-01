import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import {Product} from '../models/product';
import {CommonModule, NgFor, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {OAuthService} from 'angular-oauth2-oidc';

@Component({
  selector: 'app-search-result',
  imports: [NgIf, NgFor, CommonModule, FormsModule],
  templateUrl: './search-result.component.html',
  styleUrl: './search-result.component.css',
  standalone: true
})
export class SearchResultComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  error = '';

  constructor(private route: ActivatedRoute, private productService: ProductService, private CartService: CartService, private oauthService: OAuthService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const query = params['q'] || '';
      const minPrice = params['minPrice'];
      const maxPrice = params['maxPrice'];
      const availableOnly = params['availableOnly'] === 'true';

      this.productService.searchProducts(query, minPrice, maxPrice, availableOnly).subscribe({
        next: (data) => {
          this.products = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          alert('Filtro non valido.');
          this.isLoading = false;
        }
      });
    });
  }

  addToCart(productId: number): void {
    this.CartService.addToCart(productId).subscribe(
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

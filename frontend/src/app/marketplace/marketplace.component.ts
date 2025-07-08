import { Component, OnInit } from '@angular/core';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import {CommonModule, NgFor, NgIf} from '@angular/common';
import { OAuthService } from 'angular-oauth2-oidc';
import {Product} from '../models/product';
import {provideHttpClient} from '@angular/common/http';
import {UserService} from '../services/user.service';
import {MarketplaceService} from '../services/marketplace.service';
import {FormsModule, NgModel} from '@angular/forms';



@Component({
  selector: 'app-marketplace',
  imports: [CommonModule, NgFor, FormsModule ],
  templateUrl: './marketplace.component.html',
  styleUrl: './marketplace.component.css'
})
export class MarketplaceComponent implements OnInit{

  prodotti: Product[] = [];
  quantities: number[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  selectedProduct: Product | null = null;
  isEditMode: boolean = false;


  constructor(private ProductService: ProductService, private UserService: UserService, private MarketplaceService: MarketplaceService, private oauthService: OAuthService) { }

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
    this.MarketplaceService.getAllProducts().subscribe({
      next: (response: any) => {
        this.isLoading = false;

        if (Array.isArray(response)) {
          this.prodotti = response;
          this.quantities = this.prodotti.map(() => 1);

          if (this.prodotti.length === 0) {
            this.errorMessage = 'Non ci sono prodotti disponibili in questo shop.';
          } else {
            this.errorMessage = '';
          }

        } else if (response?.message) {
          this.prodotti = [];
          this.errorMessage = response.message;
        } else {
          this.prodotti = [];
          this.errorMessage = 'Formato di risposta non riconosciuto.';
        }
      },
      error: error => {
        this.isLoading = false;
        console.error('Errore nel caricamento dei prodotti:', error);
        this.prodotti = [];
        this.errorMessage = 'Errore nel caricamento dei prodotti.';
      }
    });
  }



  addProduct(): void {
    const newProduct: Product = {
      id: 0,
      name: '',
      description: '',
      price: 0,
      brand: '',
      quantity: 0
    };
    this.selectedProduct = newProduct;
    this.isEditMode = false;
  }

  editProduct(product: Product): void {
    this.selectedProduct = { ...product }; // copia per evitare modifiche dirette
    this.isEditMode = true;
  }

  saveProduct(): void {
    if (this.selectedProduct) {
      if (this.isEditMode) {
        this.MarketplaceService.editProduct(this.selectedProduct).subscribe({
          next: () => this.loadProducts(),
          error: err => console.error('Errore aggiornamento prodotto:', err)
        });
      } else {
        this.MarketplaceService.addProduct(this.selectedProduct).subscribe({
          next: () => this.loadProducts(),
          error: err => console.error('Errore aggiunta prodotto:', err)
        });
      }
      this.selectedProduct = null;
    }
  }

  deleteProduct(productId: number): void {
    if (confirm('Sei sicuro di voler eliminare questo prodotto?')) {
      this.MarketplaceService.deleteProduct(productId).subscribe({
        next: () => this.loadProducts(),
        error: err => console.error('Errore eliminazione prodotto:', err)
      });
    }
  }

  cancelEdit(): void {
    this.selectedProduct = null;
  }


}





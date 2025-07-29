import { Component, OnInit } from '@angular/core';
import { ProductService } from '../services/product.service';
import { CartService } from '../services/cart.service';
import {CommonModule, NgFor, NgIf} from '@angular/common';
import { OAuthService } from 'angular-oauth2-oidc';
import {provideHttpClient} from '@angular/common/http';
import {UserService} from '../services/user.service';
import {MarketplaceService} from '../services/marketplace.service';
import {FormsModule, NgModel} from '@angular/forms';

export interface Product {
  id: number;
  name: string;
  brand: string;
  description: string;
  price: number;
  quantity: number;

}

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
    this.ProductService.getAllProducts().subscribe({
      next: (response: any) => {
        this.isLoading = false;

        if (Array.isArray(response)) {
          this.prodotti = response;
          this.quantities = this.prodotti.map(() => 1);

          if (this.prodotti.length === 0) {
            this.errorMessage = 'Non ci sono prodotti disponibili in questo negozio.';
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

  AsaveProduct(): void {
    if (this.selectedProduct) {
      if (this.isEditMode) {
        this.ProductService.editProduct(this.selectedProduct).subscribe({
          next: () => this.loadProducts(),
          error: err => console.error('Errore aggiornamento prodotto:', err)
        });
      } else {
        this.ProductService.addProduct(this.selectedProduct).subscribe({
          next: () => this.loadProducts(),
          error: err => console.error('Errore aggiunta prodotto:', err)
        });
      }
      this.selectedProduct = null;
    }
  }

  saveProduct(): void {
    if (!this.selectedProduct) return;

    if (!this.selectedProduct.name || this.selectedProduct.name.length > 30 ||
      this.selectedProduct.description.length > 100 ||
      this.selectedProduct.price == null || this.selectedProduct.price < 0 ||
      this.selectedProduct.quantity == null || !Number.isInteger(this.selectedProduct.quantity) || this.selectedProduct.quantity < 0 ||
      !this.selectedProduct.brand || this.selectedProduct.brand.length > 15) {
      alert('Verifica i dati inseriti. Alcuni campi non sono validi.');
      return;
    }

    const action = this.isEditMode
      ? this.ProductService.editProduct(this.selectedProduct)
      : this.ProductService.addProduct(this.selectedProduct);

    action.subscribe({
      next: () => this.loadProducts(),
      error: err => {
        if (err.status === 400 && err.error) {
          alert('Errore di validazione:\n' + JSON.stringify(err.error, null, 2));
        } else {
          console.error('Errore aggiunta prodotto:', err);
        }
      }
    });

    this.selectedProduct = null;
  }


  deleteProduct(productId: number): void {
    if (confirm('Sei sicuro di voler eliminare questo prodotto?')) {
      this.ProductService.deleteProduct(productId).subscribe({
        next: () => this.loadProducts(),
        error: err => console.error('Errore eliminazione prodotto:', err)
      });
    }
  }

  cancelEdit(): void {
    this.selectedProduct = null;
  }


}





import { Component, OnInit } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OAuthService} from 'angular-oauth2-oidc';
import {Product} from '../models/product';
import {PurchaseService} from '../services/purchase.service';
import {CommonModule, CurrencyPipe, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';

interface ProductPurchasedDTO {
  name: string;
  quantity: number;
}

interface PurchaseDTO {
  id: number;
  date: string;
  totalPrice: number;
  products: ProductPurchasedDTO[];
}

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, CurrencyPipe],
  templateUrl: './purchase.component.html',
  styleUrl: './purchase.component.css'
})
export class PurchaseComponent implements OnInit {

  purchases: PurchaseDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService, private purchaseService : PurchaseService) { }

  ngOnInit(): void {
    if (this.oauthService.hasValidAccessToken()) {
      this.loadPurchases();
    } else {

      this.oauthService.events.subscribe(event => {
        if (event.type === 'token_received') {
          this.loadPurchases();
        }
      });
    }
  }

  loadPurchases(): void {
    this.purchaseService.getAllPurchase().subscribe(
      data => {
        this.purchases = data;
        this.isLoading = false;
      },
      error => {
        console.error('Errore nel caricamento degli acquisti:', error);
        this.errorMessage = 'Errore durante il caricamento degli acquisti.';
        this.isLoading = false;
      }
    );
  }

}


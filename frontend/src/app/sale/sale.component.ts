import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {OAuthService} from 'angular-oauth2-oidc';
import {SaleService} from '../services/sale.service';
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from '@angular/common';

interface ProductSoldDTO {
  name: string;
  price: number;
  quantity: number;
}

interface SaleDTO {
  id: number;
  date: string;
  totalPrice: number;
  products: ProductSoldDTO[];
  shippingAddress: string;
  check: boolean;
}

@Component({
  selector: 'app-sale',
  imports: [
    CurrencyPipe,
    DatePipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './sale.component.html',
  styleUrl: './sale.component.css'
})
export class SaleComponent  implements OnInit {


  sales: SaleDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService, private saleService : SaleService) { }

  ngOnInit(): void {
    if (this.oauthService.hasValidAccessToken()) {
      this.loadSales();
    } else {

      this.oauthService.events.subscribe(event => {
        if (event.type === 'token_received') {
          this.loadSales();
        }
      });
    }
  }

  loadSales(): void {
    this.saleService.getAllSales().subscribe(
      data => {
        this.sales = data;
        this.isLoading = false;
      },
      error => {
        console.error('Errore nel caricamento degli acquisti:', error);
        this.errorMessage = 'Errore durante il caricamento degli acquisti.';
        this.isLoading = false;
      }
    );
  }

  toggleCheck(sale: SaleDTO) {
    // Inverte lo stato
    sale.check = !sale.check;

    // Chiama il service per aggiornare
    this.saleService.updateSale(sale).subscribe({
      next: updated => {
        console.log("Aggiornato con successo", updated);
      },
      error: err => {
        console.error("Errore aggiornamento", err);
      }
    });
  }


}

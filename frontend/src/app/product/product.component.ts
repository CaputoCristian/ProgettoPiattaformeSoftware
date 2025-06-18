import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-prodotto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css']
})
export class ProductComponent {
  product = {
    name: '',
    description: '',
    price: 0,
    quantity: 0,
  };

  successMessage: string = '';
  errorMessage: string = '';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  aggiungiProdotto() {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.httpClient.post<{ message: string }>('http://localhost:8081/addProduct', this.product, { headers })
      .subscribe(
        response => {
          this.successMessage = response.message;
          this.errorMessage = '';
          this.product = {
            name: '',
            description: '',
            price: 0,
            quantity: 0,
          };
        },
        error => {
          console.error('Errore nell\'aggiunta del prodotto:', error);
          this.errorMessage = error.error.error || 'Errore nell\'aggiunta del prodotto.';
          this.successMessage = '';
        }
      );
  }
}

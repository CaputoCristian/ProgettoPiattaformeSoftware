import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../services/product.service';
import {Product} from '../models/product';
import {CommonModule, NgFor, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

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

  constructor(private route: ActivatedRoute, private productService: ProductService) {}

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
          this.error = 'Errore durante la ricerca.';
          this.isLoading = false;
        }
      });
    });
  }
}

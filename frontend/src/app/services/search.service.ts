import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class SearchService {
  searchQuery: string = '';
  minPrice?: number;
  maxPrice?: number;
  availableOnly: boolean = false;

  constructor(private router: Router) {}

  search(): void {
    const queryParams: any = {
      q: this.searchQuery.trim(),
    };

    if (this.minPrice != null) queryParams.minPrice = this.minPrice;
    if (this.maxPrice != null) queryParams.maxPrice = this.maxPrice;
    if (this.availableOnly) queryParams.availableOnly = true;

    this.router.navigate(['/search'], { queryParams });
  }
}

import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {CartService, CarrelloProdottoDTO} from '../services/cart.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {OAuthEvent, OAuthService} from 'angular-oauth2-oidc';
import {catchError, debounceTime, distinctUntilChanged, filter, of, Subject, takeUntil} from 'rxjs';
import {UserService} from '../services/user.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CartComponent implements OnInit, OnDestroy {

  cartItems: CarrelloProdottoDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  totalAmount: number = 0;

  // Variabili per l'ordine
  metodoPagamento: number = 1; // Valore di default
  indirizzoSpedizione: string = '';
  private refreshInterval: any;

  private destroy$ = new Subject<void>();

  constructor(
    private cartService: CartService,
    private userService: UserService,
    private oauthService: OAuthService,
    private changeDetector: ChangeDetectorRef

  ) { }

  ngOnInit(): void {
    // Caricamento iniziale degli elementi del carrello
    this.loadCartItems();

    // Sottoscrizione agli aggiornamenti del carrello
    this.cartService.cartUpdated$
      .pipe(
        debounceTime(300), // Previene aggiornamenti troppo frequenti
        distinctUntilChanged(), // Evita duplicati
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.loadCartItems();
      });

    // Aggiornamento periodico del carrello (ogni 30 secondi)
    this.refreshInterval = setInterval(() => {
      this.loadCartItems();
    }, 30000);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCartItems(): void {
    this.isLoading = true;
    this.cartService.getCartItems()
      .pipe(
        catchError(error => {
          console.error('Errore nel caricamento del carrello:', error);
          return of([]); // Ritorna un array vuoto in caso di errore
        }),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (items) => {
          // Filtra i prodotti con quantità > 0
          this.cartItems = items.filter(item => item.quantity > 0);
          this.calculateTotal();
          this.isLoading = false;
          // Forza l'aggiornamento della vista
          this.changeDetector.detectChanges();
        },
        error: (error) => {
          console.error('Errore nel caricamento del carrello:', error);
          this.cartItems = [];
          this.totalAmount = 0;
          this.isLoading = false;
          // Forza l'aggiornamento della vista
          this.changeDetector.detectChanges();
        },
        complete: () => {
          this.isLoading = false;
          // Forza l'aggiornamento della vista anche al completamento
          this.changeDetector.detectChanges();
        }
      });
  }

  calculateTotal(): void {
    this.totalAmount = this.cartItems.reduce((acc, item) =>
      acc + (item.price * item.quantity), 0);
  }

  increaseQuantity(idProdotto: number): void {
    this.cartService.plusAdding(idProdotto).subscribe(
      () => {
        const item = this.cartItems.find(item => item.id === idProdotto);
        if (item) {
          item.quantity += 1;
          this.calculateTotal();
        }
      },
      error => {
        console.error(error);
        alert('Errore durante l\'aumento della quantità.');
      }
    );
  }

  decreaseQuantity(productId: number) {
    this.cartService.minusRemoving(productId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadCartItems();
        },
        error: (error) => {
          console.error('Errore nella diminuzione della quantità:', error);
          // Mostro un messaggio di errore all'utente
        }
      });
  }

  removeItem(productId: number) {
    this.cartService.removeItem(productId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadCartItems();
        },
        error: (error) => {
          console.error('Errore nella rimozione:', error);
          // Mostro un messaggio di errore all'utente
        }
      });
  }

  emptyCart() {
    this.cartService.emptyCart()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadCartItems();
        },
        error: (error) => {
          console.error('Errore nello svuotamento del carrello:', error);
          // Mostro un messaggio di errore all'utente
        }
      });
  }

  placeOrder(): void {
    if (!this.indirizzoSpedizione) {
      alert('Per favore, inserisci un indirizzo di spedizione.');
      return;
    }

    this.cartService.buyCart(this.metodoPagamento, this.indirizzoSpedizione).subscribe(
      response => {
        alert('Ordine effettuato con successo.');
        this.cartItems = [];
        this.totalAmount = 0;
        this.indirizzoSpedizione = '';
      },
      error => {
        console.error(error);
        alert('Errore durante l\'effettuazione dell\'ordine.');
      }
    );
  }

  getShippingAddress(): void {
    this.userService.getUserAddress().subscribe({
      next: (address) => {
        // Filtra i prodotti con quantità > 0
        this.indirizzoSpedizione = address
      },
      error: (error) => {
        console.error('Errore nel caricamento dell inidrizzo:', error);
      },
    });
  }

}

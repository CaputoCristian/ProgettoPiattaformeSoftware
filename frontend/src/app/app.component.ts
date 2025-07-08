import {Component, OnInit} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {filter} from 'rxjs';
import {OAuthEvent, OAuthService} from 'angular-oauth2-oidc';
import {NgIf} from '@angular/common';
import {CartComponent} from './cart/cart.component';
import {CartService} from './services/cart.service';
import {HomepageComponent} from './homepage/homepage.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, CartComponent, HomepageComponent, HttpClientModule, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title: string = '';
  isOwner: boolean = false;

  constructor(
    private oauthService: OAuthService,
    private httpClient: HttpClient,
    private router: Router
  ) { }


  ngOnInit(): void {
    this.oauthService.events
      .pipe(filter(e => e.type === 'token_received'))
      .subscribe(() => {
        console.log('Token ricevuto, facendo partire la chiamata...');
        this.userInit();

      });
    if (this.oauthService.hasValidAccessToken()) {
      console.log('Token già presente, facendo partire la chiamata...');
      this.userInit();

    }
  }

  userInit() {
    const token = this.oauthService.getAccessToken();
    this.httpClient.get<{ message: string }>('http://localhost:8081/home', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }).subscribe(response => {
      console.log(response);
    });
  }

  isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  logout(): void {
    this.oauthService.logOut();
  }

}

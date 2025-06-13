import {Component, OnInit} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {HeaderComponent} from "./header/header.component";
import {HttpClient} from '@angular/common/http';
import {filter} from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
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
  }

  userInit() {
    const token = this.oauthService.getAccessToken();
    this.httpClient.get<{ message: string }>('http://localhost:8080/home', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }).subscribe(response => {
      console.log(response);
    });
  }

    login() {
    this.oauthService.initLoginFlow();
  }

  isLoggedIn(): boolean {
    return this.oauthService.hasValidAccessToken();
  }


}

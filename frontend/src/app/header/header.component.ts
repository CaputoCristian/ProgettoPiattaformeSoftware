import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {LoginComponent} from '../login/login.component';
import {AuthService} from '../auth.service';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,CommonModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  constructor(private authService: AuthService) {}


  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}


import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {Router, RouterModule} from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  login(): void {
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        localStorage.setItem('access_token', res.access_token);
        console.log('Login riuscito:', res);
        const token = res.access_token;
        // redirect, nav, ecc.
        alert('Login riuscito!');
        alert(token);
      },
      error: (err) => {
        alert('Login fallito!');
        this.errorMessage = 'Login fallito. Verifica email e password.';
        console.error('Errore login', err);
      },
    });
  }
}

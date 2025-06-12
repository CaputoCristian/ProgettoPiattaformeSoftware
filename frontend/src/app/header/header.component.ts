import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {LoginComponent} from '../login/login.component';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

}

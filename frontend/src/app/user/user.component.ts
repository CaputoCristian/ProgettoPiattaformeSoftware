import {Component, OnInit} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import {UserService, UserProfileDTO} from '../services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {CarrelloProdottoDTO} from '../services/cart.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})

export class UserComponent implements OnInit {

  userProfile: UserProfileDTO = {
    firstName: '',
    lastName: '',
    email: '',
    address: '',
    telephoneNumber: '',
    birthDate: ''
  };
  originalProfile!: UserProfileDTO;
  isLoading: boolean = true;
  errorMessage: string = '';
  isEditing = false;
  editableProfile: UserProfileDTO = {} as UserProfileDTO;


  constructor(private UserService: UserService) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    this.UserService.getUser().subscribe(
      user => {
        this.userProfile = user;
        this.originalProfile = user;
        this.editableProfile = user;
        this.isLoading = false;
      },
      error => {
        console.error(error);
        this.errorMessage = 'Errore nel caricamento del profilo utente.';
        this.isLoading = false;
      }
    );
  }

  enableEdit() {
    this.isEditing = true;
  }

  cancelEdit() {
    this.editableProfile = { ...this.userProfile };
    this.isEditing = false;
  }

  saveChanges() {
    this.UserService.updateUser(this.editableProfile).subscribe(updated => {
      this.userProfile = updated;
      this.editableProfile = updated
      this.isEditing = false;
    });
  }

}

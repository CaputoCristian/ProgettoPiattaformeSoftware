import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    const isLoggedIn = await this.keycloakService.isLoggedIn();

    if (!isLoggedIn) {
      this.keycloakService.login({ redirectUri: window.location.origin + state.url });
      return false;
    }

    const requiredRoles = route.data['roles'];
    if (requiredRoles && requiredRoles.length > 0) {
      const userRoles = this.keycloakService.getUserRoles();
      const hasRole = requiredRoles.some((role: string) => userRoles.includes(role));
      if (!hasRole) {
        this.router.navigate(['/home']);
        return false;
      }
    }

    return true;
  }
}

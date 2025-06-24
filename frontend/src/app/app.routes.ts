import {RouterModule, Routes} from '@angular/router';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin/admin.component';
import {LoginComponent} from './login/login.component';
import {NgModule} from '@angular/core';
import {AuthGuard} from './guards/auth.guard';
import {AppComponent} from './app.component';
import {HomepageComponent} from './homepage/homepage.component';
import {ProductComponent} from './product/product.component';

export const routes: Routes = [
  { path: 'products', component:HomepageComponent, canActivate: [AuthGuard]},
  { path: 'user', component:UserComponent},
  { path: 'admin', component:AdminComponent},
  { path: 'login', component:LoginComponent },
  { path: 'prodotto', component:ProductComponent },


];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {}

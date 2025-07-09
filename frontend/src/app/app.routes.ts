import {RouterModule, Routes} from '@angular/router';
import {UserComponent} from './user/user.component';
import {AdminComponent} from './admin/admin.component';
import {LoginComponent} from './login/login.component';
import {NgModule} from '@angular/core';
import {AuthGuard} from './guards/auth.guard';
import {AppComponent} from './app.component';
import {HomepageComponent} from './homepage/homepage.component';
import {ProductComponent} from './product/product.component';
import {MarketplaceComponent} from './marketplace/marketplace.component';
import {SearchResultComponent} from './search-result/search-result.component';

export const routes: Routes = [
  { path: '', component:HomepageComponent, pathMatch: 'full'},
  { path: 'user', component:UserComponent},
  { path: 'login', component:LoginComponent },
  { path: 'product', component:ProductComponent },
  { path: 'shop', component:MarketplaceComponent },
  { path: 'search', component: SearchResultComponent }

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {}

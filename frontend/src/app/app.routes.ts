import {RouterModule, Routes} from '@angular/router';
import {UserComponent} from './user/user.component';
import {NgModule} from '@angular/core';
import {HomepageComponent} from './homepage/homepage.component';
import {ProductComponent} from './product/product.component';
import {MarketplaceComponent} from './marketplace/marketplace.component';
import {SearchResultComponent} from './search-result/search-result.component';
import {PurchaseComponent} from './purchase/purchase.component';
import {SaleComponent} from './sale/sale.component';

export const routes: Routes = [
  { path: '', component:HomepageComponent, pathMatch: 'full'},
  { path: 'user', component:UserComponent},
  { path: 'product', component:ProductComponent },
  { path: 'shop', component:MarketplaceComponent },
  { path: 'search', component: SearchResultComponent },
  { path: 'purchase', component: PurchaseComponent},
  { path: 'sales', component: SaleComponent}

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {}

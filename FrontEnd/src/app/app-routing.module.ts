import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MenuComponent} from "./components/menu/menu.component";
import {authGuard} from "./guards/auth/auth.guard";
import {ConnectingComponent} from "./components/connecting/connecting.component";

const routes: Routes = [
  { path: 'home',
    component: MenuComponent
  },
  {
    path: 'connecting',
    component: ConnectingComponent
  },
  {
    path: 'match',
    loadChildren: () => import('./match/match.module').then(m => m.MatchModule),
    canActivate: [authGuard]},
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

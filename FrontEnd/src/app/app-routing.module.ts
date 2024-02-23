import {inject, NgModule} from '@angular/core';
import {NavigationEnd, Router, RouterModule, Routes} from '@angular/router';
import {MenuComponent} from "./components/menu/menu.component";
import {authGuard} from "./guards/auth/auth.guard";
import {ConnectingComponent} from "./components/connecting/connecting.component";
import {SessionService} from "./services/session.service";
import {filter} from "rxjs";

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
export class AppRoutingModule {
  private router: Router = inject(Router);
  private sessionService: SessionService = inject(SessionService);

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(
      value => {
        const event = value as NavigationEnd;
        if(event.url === '/match' && (event.urlAfterRedirects === '/home' || event.urlAfterRedirects === '/')) {
          this.sessionService.logOut();
        }
      }
    );
  }
}

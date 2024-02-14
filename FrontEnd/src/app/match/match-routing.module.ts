import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MatchMenuComponent} from "./components/match-menu/match-menu.component";

const routes: Routes = [{ path: '', component: MatchMenuComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MatchRoutingModule { }

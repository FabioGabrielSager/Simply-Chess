import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MatchMenuComponent} from "./components/match-menu/match-menu.component";
import {MatchComponent} from "./components/match/match.component";
import {MatchQueueComponent} from "./components/match-queue/match-queue.component";

const routes: Routes = [
  {
    path: '',
    component: MatchMenuComponent
  },
  {
    path: 'current/:id',
    component: MatchComponent,
  },
  {
    path: 'queue',
    component: MatchQueueComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MatchRoutingModule {
}

import {Component, inject, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {SessionService} from "../../../services/session.service";
import {MatchSessionService} from "../../services/match-session.service";

@Component({
  selector: 'app-match-menu',
  templateUrl: './match-menu.component.html',
  styleUrls: ['./match-menu.component.css']
})
export class MatchMenuComponent implements OnInit {
  private sessionService: SessionService = inject(SessionService);
  private matchService: MatchSessionService = inject(MatchSessionService);
  private router: Router = inject(Router);
  isConnecting: boolean = this.matchService.isConnecting;
  username: string = this.sessionService.getUserName();
  loadingPageTitle: string = "";

  ngOnInit(): void {
    this.matchService.isConnectingSubject.subscribe(
      value => {
        this.isConnecting = value;
        if (!value) {
          this.router.navigate(["match/current/" + this.matchService.match?.id]);
        }
      });
  }

  logOut() {
    this.sessionService.logOut();
    this.router.navigate(['/home'])
  }

  searchMatch() {
    // TODO: IMPLEMENT LOGIC TO ENTER ON THE MATCH QUEUE

    this.router.navigate(['/match/queue'])
  }

  createMatch() {
    this.matchService.createMatch();
    this.loadingPageTitle = "Creando partida";
  }
}

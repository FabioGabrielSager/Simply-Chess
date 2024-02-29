import {Component, inject, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {SessionService} from "../../../services/session.service";
import {MatchSessionService} from "../../services/match-session.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {MatchIdModalComponent} from "../match-id-modal/match-id-modal.component";

@Component({
  selector: 'app-match-menu',
  templateUrl: './match-menu.component.html',
  styleUrls: ['./match-menu.component.css']
})
export class MatchMenuComponent implements OnInit {
  private sessionService: SessionService = inject(SessionService);
  private matchService: MatchSessionService = inject(MatchSessionService);
  private modalService: NgbModal = inject(NgbModal);
  private router: Router = inject(Router);
  isConnecting: boolean = this.matchService.isConnecting;
  username: string = this.sessionService.getUserName();
  loadingPageTitle: string = "";

  ngOnInit(): void {
    this.matchService.isConnectingSubject.subscribe(
      {
        next: value => {
          this.isConnecting = value;
          if (!value) {
            this.router.navigate(["match/current/" + this.matchService.match?.id]);
          }
        },
        error: err => {
          this.isConnecting = false;
        }
      });
  }

  logOut() {
    this.sessionService.logOut();
    this.router.navigate(['/home'])
  }

  searchMatch() {
    this.router.navigate(['/match/queue'])
  }

  createMatch() {
    this.matchService.createMatch();
    this.loadingPageTitle = "Creando partida";
  }

  connectMatch() {
    this.loadingPageTitle = "Conectando";
    this.modalService.open(MatchIdModalComponent, {centered: true})
  }
}

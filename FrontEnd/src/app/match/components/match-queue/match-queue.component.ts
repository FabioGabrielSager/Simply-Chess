import {Component, inject, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {MatchSessionService} from "../../services/match-session.service";

@Component({
  selector: 'app-match-queue',
  standalone: true,
  imports: [],
  templateUrl: './match-queue.component.html',
  styleUrl: './match-queue.component.css'
})
export class MatchQueueComponent implements OnInit {
  private matchService: MatchSessionService = inject(MatchSessionService);
  private router: Router = inject(Router);

  ngOnInit(): void {
    this.matchService.isConnectingSubject.subscribe(
      {
        next: value => {
          if (!value) {
            this.router.navigate(["match/current/" + this.matchService.match?.id]);
          }
        },
        error: err => {
          this.router.navigate(['/match']);
        }
      });
    this.matchService.connectMatchQueue();
  }

  leaveQueue() {
    this.matchService.leaveMatchQueue();
    this.router.navigate(['/match']);
  }
}

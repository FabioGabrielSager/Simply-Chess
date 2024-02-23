import {Component, inject, OnInit} from '@angular/core';
import {MatchSessionService} from "../../services/match-session.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-creating-match',
  standalone: true,
  imports: [],
  templateUrl: './creating-match.component.html',
  styleUrl: './creating-match.component.css'
})
export class CreatingMatchComponent implements OnInit {
  private matchService: MatchSessionService = inject(MatchSessionService);
  private router: Router = inject(Router);
  ngOnInit(): void {
    if(this.matchService.isConnecting) {
      this.matchService.isConnectingSubject.subscribe(
        value => {
          if(!value) {
            this.router.navigate(["match/current/" + this.matchService.match?.id])
          }
        }
      )
    } else {
      this.router.navigate(["match/current/" + this.matchService.match?.id])
    }
  }


}

import {Component, inject} from '@angular/core';
import {Router} from "@angular/router";
import {SessionService} from "../../../services/session.service";

@Component({
  selector: 'app-match-menu',
  templateUrl: './match-menu.component.html',
  styleUrls: ['./match-menu.component.css']
})
export class MatchMenuComponent {
  private sessionService: SessionService = inject(SessionService);
  private router: Router = inject(Router);
  username: string = this.sessionService.getUserName();

  logOut() {
    this.sessionService.logOut();
    this.router.navigate(['/home'])
  }

  searchMatch() {
    // TODO: IMPLEMENT LOGIC TO ENTER ON THE MATCH QUEUE

    this.router.navigate(['/match/queue'])
  }
}

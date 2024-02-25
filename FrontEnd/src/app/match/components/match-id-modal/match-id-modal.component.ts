import {Component, inject} from '@angular/core';
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {SessionService} from "../../../services/session.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatchSessionService} from "../../services/match-session.service";

@Component({
  selector: 'app-match-id-modal',
  standalone: true,
    imports: [
        FormsModule,
        NgIf,
        ReactiveFormsModule
    ],
  templateUrl: './match-id-modal.component.html',
  styleUrl: './match-id-modal.component.css'
})
export class MatchIdModalComponent {
  modal: NgbActiveModal = inject(NgbActiveModal);
  private matchService: MatchSessionService = inject(MatchSessionService);
  private router: Router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);
  matchId: string = "";

  login(form: NgForm) {
    if(form.invalid) {
      form.control.get('username')?.markAsDirty()
      return;
    }

    this.matchService.connectMatch(this.matchId);
    this.modal.close();
    this.router.navigate(["connecting"]);
  }

  onKeyDown(key: any, form: NgForm) {
    if(key.key == 'Enter') {
      this.login(form)
    }
  }
}

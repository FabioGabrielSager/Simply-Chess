import {Component, inject} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {SessionService} from "../../services/session.service";
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-username-modal',
  templateUrl: './username-modal.component.html',
  styleUrls: ['./username-modal.component.css']
})
export class UsernameModalComponent{
  modal: NgbActiveModal = inject(NgbActiveModal);
  private sessionService: SessionService = inject(SessionService);
  private router: Router = inject(Router);
  username: string = "";

  login(form: NgForm) {
    if(form.invalid) {
      form.control.get('username')?.markAsDirty()
      return;
    }

    this.sessionService.saveUser(this.username);
    this.modal.close();
    this.router.navigate(["match"]);
  }
}

import {Component, inject} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {SessionService} from "../../services/session.service";
import {NgForm} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-username-modal',
  templateUrl: './username-modal.component.html',
  styleUrls: ['./username-modal.component.css']
})
export class UsernameModalComponent{
  modal: NgbActiveModal = inject(NgbActiveModal);
  private sessionService: SessionService = inject(SessionService);
  private router: Router = inject(Router);
  private route: ActivatedRoute = inject(ActivatedRoute);
  username: string = "";

  login(form: NgForm) {
    if(form.invalid) {
      form.control.get('username')?.markAsDirty()
      return;
    }

    this.sessionService.saveUser(this.username);
    this.modal.close();
  }

  onKeyDown(key: any, form: NgForm) {
    if(key.key == 'Enter') {
      this.login(form)
    }
  }
}

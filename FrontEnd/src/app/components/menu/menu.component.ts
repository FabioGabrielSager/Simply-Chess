import {Component, inject, OnInit} from '@angular/core';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UsernameModalComponent} from "../username-modal/username-modal.component";
import {SessionService} from "../../services/session.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  private modalService: NgbModal = inject(NgbModal);
  private sessionService: SessionService = inject(SessionService);
  private router: Router = inject(Router);
  isConnecting: boolean = this.sessionService.isConnecting;

  ngOnInit(): void {
    this.sessionService.isConnectingSubject.subscribe(
      value => {
        this.isConnecting = value;
        if (!value) {
          this.router.navigate(["match"]);
        }
      });
  }

  onClickPlay() {
    this.modalService.open(UsernameModalComponent, {centered: true})
  }
}

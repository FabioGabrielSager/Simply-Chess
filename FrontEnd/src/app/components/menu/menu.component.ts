import {Component, inject} from '@angular/core';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UsernameModalComponent} from "../username-modal/username-modal.component";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent {
  private modalService: NgbModal = inject(NgbModal);

  onClickPlay() {
    this.modalService.open(UsernameModalComponent, { centered: true })
  }
}

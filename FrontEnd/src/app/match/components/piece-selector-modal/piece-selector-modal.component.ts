import {Component, inject} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {MatchSessionService} from "../../services/match-session.service";

@Component({
  selector: 'app-piece-selector-modal',
  standalone: true,
    imports: [
        FormsModule,
        NgIf
    ],
  templateUrl: './piece-selector-modal.component.html',
  styleUrl: './piece-selector-modal.component.css'
})
export class PieceSelectorModalComponent {
  private matchService: MatchSessionService = inject(MatchSessionService);
  modal: NgbActiveModal = inject(NgbActiveModal);

  onPieceSelected(span: HTMLSpanElement) {
    this.matchService.sendPromotionRequest(span.id);
    this.modal.dismiss();
  }
}

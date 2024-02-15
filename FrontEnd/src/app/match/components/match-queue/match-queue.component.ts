import {Component, inject} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-match-queue',
  standalone: true,
  imports: [],
  templateUrl: './match-queue.component.html',
  styleUrl: './match-queue.component.css'
})
export class MatchQueueComponent {
  private router: Router = inject(Router);

  leaveQueue() {
    this.router.navigate(['/match'])
  }
}

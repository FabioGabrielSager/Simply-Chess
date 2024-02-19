import {Component, inject, OnInit} from '@angular/core';
import {SessionService} from "../../services/session.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-connecting',
  templateUrl: './connecting.component.html',
  styleUrl: './connecting.component.css'
})
export class ConnectingComponent implements OnInit{
  private sessionService: SessionService = inject(SessionService);
  private router: Router = inject(Router);

  ngOnInit(): void {
    if(this.sessionService.isConnecting) {
      this.sessionService.isConnectingSubject.subscribe(
        value => {
        if(!value) {
          this.router.navigate(["match"]);
        }
      })
    } else {
      this.router.navigate(["match"]);
    }
  }


}

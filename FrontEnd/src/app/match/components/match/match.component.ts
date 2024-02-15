import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {map, Subscription} from "rxjs";
import {ClipboardService} from "ngx-clipboard";
import {ToastService} from "../../../services/toast.service";

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css']
})
export class MatchComponent implements OnInit, OnDestroy {
  private route: ActivatedRoute = inject(ActivatedRoute);
  private clipboardService: ClipboardService = inject(ClipboardService);
  private toastService: ToastService = inject(ToastService);
  private subs : Subscription = new Subscription();
  matchId: string = "";
  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe(
        (params: Params) => {
          this.matchId = params['id'];
        }
      )
    );

  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  onCopy() {
    this.clipboardService.copy(this.matchId);
    this.toastService.show("ID Copiada!", "bg-success")
  }
}

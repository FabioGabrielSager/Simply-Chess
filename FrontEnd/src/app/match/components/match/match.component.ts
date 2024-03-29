import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {interval, Subscription} from "rxjs";
import {ClipboardService} from "ngx-clipboard";
import {ToastService} from "../../../services/toast.service";
import {MatchSessionService} from "../../services/match-session.service";
import {PieceMovement} from "../../models/piece-movement";

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css']
})
export class MatchComponent implements OnInit, OnDestroy {
  private route: ActivatedRoute = inject(ActivatedRoute);
  private clipboardService: ClipboardService = inject(ClipboardService);
  private toastService: ToastService = inject(ToastService);
  matchService: MatchSessionService = inject(MatchSessionService);
  private subs: Subscription = new Subscription();
  matchId: string | undefined = "";
  rivalPlayerName: string = "";
  playerName: string = "";

  interval = interval(1000);
  timer: { minutes: number, seconds: number } = {minutes: 0, seconds: 0}

  ngOnInit(): void {
    this.matchId = this.matchService.match?.id;
    if (this.matchService.match != null) {
      if (this.matchService.playerTeamColor == "BLACK") {
        this.playerName = this.matchService.match.blackPlayer;
        this.rivalPlayerName = this.matchService.match.whitePlayer;
      } else {
        this.playerName = this.matchService.match.whitePlayer;
        this.rivalPlayerName = this.matchService.match.blackPlayer;
      }

      if (this.matchService.match.blackPlayer != undefined && this.matchService.match.whitePlayer != undefined) {
        this.initTimer();
      }
    }

    this.subs.add(
      this.route.params.subscribe(
        (params: Params) => {
          this.matchId = params['id'];
        }
      )
    );

    this.subs.add(this.matchService.rivalIsConnectedSubject.subscribe(
      value => {
        if (value) {
          if (this.matchService.match != null) {
            this.rivalPlayerName = this.matchService.playerTeamColor == "BLACK" ?
              this.matchService.match.whitePlayer : this.matchService.match.blackPlayer;
            this.initTimer();
          }
        }
      }
    ));

  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  onCopy() {
    if (this.matchId != undefined) {
      this.clipboardService.copy(this.matchId);
    }
    this.toastService.show("ID Copiada!", "bg-success")
  }

  onPieceMove($event: PieceMovement) {
    this.matchService.gameplay($event);
  }

  private initTimer() {
    this.subs.add(
      this.interval.subscribe(
        value => {
          if (this.timer.seconds < 60)
            this.timer.seconds++;
          else {
            this.timer.seconds = 0;
            this.timer.minutes++;
          }
        }
      )
    );
  }
}

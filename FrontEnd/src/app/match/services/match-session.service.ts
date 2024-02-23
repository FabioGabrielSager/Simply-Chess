import {inject, Injectable, OnDestroy, OnInit} from '@angular/core';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {HttpClient} from "@angular/common/http";
import {SessionService} from "../../services/session.service";
import {Observable, Subject, Subscription} from "rxjs";
import {MatchWithPlayerTeam} from "../../models/match-with-player-team";
import {Match} from "../../models/match";
import {User} from "../../models/User";

@Injectable({
  providedIn: 'root'
})
export class MatchSessionService implements OnInit, OnDestroy {

  private httpClient: HttpClient = inject(HttpClient);
  private stompClient: any;
  private sessionService: SessionService = inject(SessionService);
  private subs: Subscription = new Subscription();
  isConnectingSubject: Subject<boolean> = new Subject<boolean>();
  private _isConnecting: boolean = false;
  match: Match | null = null;
  playerTeamColor: string | undefined = undefined;

  constructor() {
    this.setUpSocketConnection();
  }

  ngOnInit(): void {
  }

  get isConnecting() {
    return this._isConnecting;
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  createMatch(): void {
    this._isConnecting = true;
    const user = {
      id: "c8783778-4c6b-43c4-a057-82f3b31ee68a",
      name: "Fabio"
    } as User

    this.subs.add(
      this.httpClient.post<MatchWithPlayerTeam>("http://localhost:8080/match/create", user)
        .subscribe(
          {
            next: value => {
              this.match = value.match;
              this.playerTeamColor = value.playerTeam;
              this._isConnecting = false;
              this.isConnectingSubject.next(false);
              this.stompClient.connect({}, () => {
                  this.stompClient.subscribe("/match/queue/game-progress/", (payload: any) => {
                    this.onMatchUpdate(payload);
                  });
                }
              )
            },
            error: err => {
              console.log("error", err);
              this._isConnecting = false;
              this.isConnectingSubject.next(false);
            }
          }
        )
    );
  }

  connectMatch(matchId: string): Observable<MatchWithPlayerTeam> {
    const requestBody = {
      player: this.sessionService.user,
      gameId: matchId
    };
    return this.httpClient.post<MatchWithPlayerTeam>("http://localhost:8080/match/connect", requestBody);
  }

  private onMatchUpdate(payload: any) {
    this.match = JSON.parse(payload.body) as Match;
  }

  private setUpSocketConnection() {
    const url = "//localhost:8080/chess-match";
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
  }

}

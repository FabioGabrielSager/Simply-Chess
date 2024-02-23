import {inject, Injectable, OnDestroy, OnInit} from '@angular/core';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {HttpClient} from "@angular/common/http";
import {SessionService} from "../../services/session.service";
import {Observable, Subject, Subscription} from "rxjs";
import {MatchWithPlayerTeam} from "../../models/match-with-player-team";
import {Match} from "../../models/match";
import {User} from "../../models/User";
import {ToastService} from "../../services/toast.service";

@Injectable({
  providedIn: 'root'
})
export class MatchSessionService implements OnInit, OnDestroy {

  private toastService: ToastService = inject(ToastService);
  private httpClient: HttpClient = inject(HttpClient);
  private stompClient: any;
  private sessionService: SessionService = inject(SessionService);
  private subs: Subscription = new Subscription();
  isConnectingSubject: Subject<boolean> = new Subject<boolean>();
  rivalIsConnectedSubject: Subject<boolean> = new Subject<boolean>();
  private rivalIsConnected: boolean = false;
  private _isConnecting: boolean = false;
  match: Match | null = null;
  playerTeamColor: string | undefined = undefined;

  constructor() {
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
    this.subs.add(
      this.httpClient.post<MatchWithPlayerTeam>("http://localhost:8080/match/create", this.sessionService.user)
        .subscribe(
          {
            next: value => {
              this.setUpSocketConnection();
              this.stompClient.connect({}, () => {
                  this.stompClient.subscribe("/match/queue/game-progress/" + value.match.id, (payload: any) => {
                    this.onMatchUpdate(payload);
                  });
                  this.match = value.match;
                  this.playerTeamColor = value.playerTeam;
                  this._isConnecting = false;
                  this.isConnectingSubject.next(false);
                  this.match = value.match;
                },
              (error: any) => {
                this.toastService.show("Hubo un error al intentar conectarse, inténtelo mas tarde",
                  "bg-danger");
                console.log(error);
              });
            },
            error: err => {
              this.toastService.show("Hubo un error al intentar crear la partida, inténtelo mas tarde",
                "bg-danger");
              console.log(err);
              this.match = null;
              this.playerTeamColor = undefined;
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
    if(!this.rivalIsConnected) {
      this.rivalIsConnectedSubject.next(true);
      this.rivalIsConnected = true;
    }

    this.match = JSON.parse(payload.body) as Match;
  }

  // TODO: ADD LOGIC TO UPDATE SESSION VALUES ON A MATCH DISCONNECTION.

  private setUpSocketConnection() {
    const url = "//localhost:8080/chess-match";
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
  }

}

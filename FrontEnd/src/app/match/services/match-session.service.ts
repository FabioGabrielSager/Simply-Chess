import {inject, Injectable, OnDestroy, OnInit} from '@angular/core';
import * as SockJS from "sockjs-client";
import {FrameImpl, Stomp} from "@stomp/stompjs";
import {HttpClient} from "@angular/common/http";
import {SessionService} from "../../services/session.service";
import {Subject, Subscription} from "rxjs";
import {MatchWithPlayerTeam} from "../../models/match-with-player-team";
import {Match} from "../../models/match";
import {ToastService} from "../../services/toast.service";
import {GameStatus} from "../../models/game-status";
import Swal from "sweetalert2";
import {Router} from "@angular/router";
import {PlayerInQueueResponse} from "../../models/playerInQueueResponse";
import {User} from "../../models/user";


@Injectable({
  providedIn: 'root'
})
export class MatchSessionService implements OnInit, OnDestroy {

  private toastService: ToastService = inject(ToastService);
  private stompClient: any;
  private httpClient: HttpClient = inject(HttpClient);
  private sessionService: SessionService = inject(SessionService);
  private router: Router = inject(Router);
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
    this.isConnectingSubject.next(true);

    this.setUpSocketConnection();
    this.stompClient.connect({}, () => {
        this.stompClient.subscribe("/user/queue/reply", (payload: FrameImpl) => {
          const value: MatchWithPlayerTeam = JSON.parse(payload.body);
          this.stompClient.subscribe("/queue/game-progress/" + value.match.id, (payload: FrameImpl) => {
            const match: Match = JSON.parse(payload.body);
            console.log(match.status !== GameStatus.FINISHED, match.status != GameStatus.FINISHED)
            if (match.status !== GameStatus.FINISHED && match.status !== GameStatus.TIED) {
              this.onMatchUpdate(match);
            } else {
              this.onMatchFinish(match);
            }
          });
          this.match = value.match;
          this.playerTeamColor = value.playerTeam;
          this._isConnecting = false;
          this.isConnectingSubject.next(false);
          this.match = value.match;
        });
        this.stompClient.send("/match/create", {}, JSON.stringify(this.sessionService.user));
      },
      (error: any) => {
        this.toastService.show("Hubo un error al intentar conectarse, inténtelo mas tarde",
          "bg-danger");
        console.log(error);
        this.onUnsuccessfulConnection(error);
      });
  }

  connectMatch(matchId: string): void {
    const requestBody = {
      player: this.sessionService.user,
      gameId: matchId
    };

    this._isConnecting = true;
    this.isConnectingSubject.next(true);
    this.rivalIsConnected = true;
    this.setUpSocketConnection();
    this.stompClient.connect({}, () => {
        this.stompClient.subscribe("/user/queue/reply", (payload: FrameImpl) => {
          this.playerTeamColor = payload.body.substring(1, payload.body.length - 1);
          this._isConnecting = false;
          this.isConnectingSubject.next(false);
        });

        this.stompClient.subscribe("/queue/game-progress/" + matchId, (payload: FrameImpl) => {
          const match: Match = JSON.parse(payload.body);
          if (!(match.status == GameStatus.FINISHED || match.status == GameStatus.TIED)) {
            this.onMatchUpdate(match);
          } else {
            this.onMatchFinish(match);
          }
        });
        this.stompClient.send("/match/connect", {}, JSON.stringify(requestBody))
      },
      (error: any) => {
        this.toastService.show("Hubo un error al intentar conectarse, inténtelo mas tarde",
          "bg-danger");
        console.log(error);
        this.onUnsuccessfulConnection(error);
      });
  }

  connectMatchQueue() {
    this._isConnecting = true;
    this.isConnectingSubject.next(true);
    this.subs.add(
      this.httpClient.post<PlayerInQueueResponse>("http://localhost:8080/match/connect/matchesQueue",
        this.sessionService.user).subscribe(
        {
          next: value => {
            this.setUpSocketConnection();
            this.stompClient.connect({}, () => {
                this.stompClient.subscribe("/queue/game-queue/" + value.queueId, (payload: FrameImpl) => {
                  const response: MatchWithPlayerTeam = JSON.parse(payload.body);
                  this.match = response.match;
                  this.playerTeamColor = response.playerTeam;

                  this.stompClient.subscribe("/queue/game-progress/" + response.match.id, (payload: FrameImpl) => {
                    const match: Match = JSON.parse(payload.body);
                    if (!(match.status == GameStatus.FINISHED || match.status == GameStatus.TIED)) {
                      this.onMatchUpdate(match);
                    } else {
                      this.onMatchFinish(match);
                    }
                  });

                  this._isConnecting = false;
                  this.isConnectingSubject.next(false);
                });
              },
              (error: any) => {
                this.toastService.show("Hubo un error al intentar conectarse a la cola, inténtelo mas tarde",
                  "bg-danger");
                console.log(error);
                this.onUnsuccessfulConnection(error);
              })
          },
          error: err => {
            this.toastService.show("Hubo un error al intentar entrar en cola, inténtelo mas tarde",
              "bg-danger");
            console.log(err);
            this.onUnsuccessfulConnection(err);
          }
        }
      )
    );
  }

  leaveMatchQueue() {
    this._isConnecting = false;
    this.stompClient.deactivate();
  }

  private onMatchUpdate(match: Match) {
    this.match = match;
    if (!this.rivalIsConnected) {
      this.rivalIsConnectedSubject.next(true);
      this.rivalIsConnected = true;
    }
  }

  private onUnsuccessfulConnection(error: Error) {
    this.match = null;
    this.playerTeamColor = undefined;
    this._isConnecting = false;
    this.rivalIsConnected = false;
    this.isConnectingSubject.error(error);

    if (this.stompClient.connected) {
      this.stompClient.deactivate();
    }
  }

  private onMatchFinish(match: Match) {
    Swal.fire({
      title: "El juego a finalizado",
      text: "Razón: " + match.finishReason,
      color: "rgb(255, 196, 122)",
      background: "#261F22",
      icon: "info",
      confirmButtonText: "<span style='color: rgb(255, 196, 122)'>Confirmar</span>",
      confirmButtonColor: "#4E2C0B",
    });

    this.stompClient.deactivate();

    this.match = null;
    this.playerTeamColor = undefined;
    this._isConnecting = false;
    this.rivalIsConnected = false;

    this.router.navigate(['match']);
  }

  private setUpSocketConnection() {
    const url = "//localhost:8080/chess-match";
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
  }

}

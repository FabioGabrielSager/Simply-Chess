import {inject, Injectable, OnDestroy} from '@angular/core';
import {User} from "../models/User";
import {HttpClient} from "@angular/common/http";
import {Observable, of, Subject, Subscription} from "rxjs";
import {ToastService} from "./toast.service";
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  private toastService: ToastService = inject(ToastService);
  private _user: User = new User();

  private httpClient: HttpClient = inject(HttpClient);
  private stompClient: any;
  isConnectingSubject: Subject<boolean> = new Subject<boolean>();
  private _isConnecting: boolean = false;

  constructor() {
  }

  get isConnecting() {
    return this._isConnecting;
  }

  get user(): User {
    return this._user;
  }

  saveUser(username: string) {
    this._isConnecting = true;
    this.setupSocketConnection();

    this.stompClient.connect({}, () => {
        this.stompClient.subscribe("/user/queue/reply", (payload: any) => {
          const user = JSON.parse(payload.body);
          this._user.name = user.name;
          this._user.id = user.id;
          this._isConnecting = false;
          this.isConnectingSubject.next(false);
        });
        this.stompClient.send("/player/add", {}, username);
      },
      (error: any) => {
        this.toastService.show("Hubo un error durante la conexión, inténtelo más tarde", "bg-danger");
        console.log(error);
      },
      (close: any) => {
        if (close.code == 1002) {
          this.toastService.show("Hubo un error durante la conexión, inténtelo más tarde", "bg-danger");
          this._isConnecting = false;
          this.isConnectingSubject.next(false);
        }
      });
  }

  private setupSocketConnection() {
    const url = "//localhost:8081/chess-player";
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);
  }

  logOut() {
    this.stompClient.deactivate();
    this._user.id = "";
    this._user.name = "";
  }

  isLogged(): Observable<boolean> {
    return new Observable<boolean>((observer) => {
      this.httpClient.get<boolean>(`http://localhost:8081/exists/?id=${this._user.id}&name=${this._user.name}`)
        .subscribe(
          {
            next: value => {
              observer.next(this._user.id !== "" && this._user.name !== "" && this.stompClient.connected && value);
              observer.complete();
            },
            error: err => {
              observer.error(err)
              this.toastService.show("Hubo un error al comprobar el estado de su sesión",
                "bg-danger");
              console.log(err);
            }
          }
        )
    })
  }

  getUserName() {
    return this._user.name;
  }
}

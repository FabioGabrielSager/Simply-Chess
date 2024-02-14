import { Injectable } from '@angular/core';
import {User} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private user: User = new User();
  constructor() { }

  saveUser(username: string) {
    this.user.name = username;
    // TODO: Implements the logic to persists the new user.
  }

  isLogged() {
    return this.user.id != "" && this.user.name != "";
  }
}

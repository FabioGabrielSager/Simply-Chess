import { Injectable } from '@angular/core';
import {User} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private user: User = new User();
  constructor() {
    this.user.name = "user";
    this.user.id = "user"
  }

  saveUser(username: string) {
    this.user.name = username;
    this.user.id = "asdasd";
    // TODO: Implements the logic to persists the new user.
  }

  logOut() {
    // TODO: Implements the logic to delete the user.
  }

  isLogged() {
    return this.user.id != "" && this.user.name != "";
  }

  getUserName() {
    return this.user.name;
  }
}

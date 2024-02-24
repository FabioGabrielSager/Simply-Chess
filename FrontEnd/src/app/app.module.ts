import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MenuComponent } from './components/menu/menu.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { UsernameModalComponent } from './components/username-modal/username-modal.component';
import {FormsModule} from "@angular/forms";
import {ToastContainerComponent} from "./components/toast-container/toast-container.component";
import {HttpClientModule} from "@angular/common/http";
import {MatchModule} from "./match/match.module";
import {LoadingComponent} from "./components/loading/loading.component";

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    UsernameModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    FormsModule,
    ToastContainerComponent,
    HttpClientModule,
    MatchModule,
    LoadingComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

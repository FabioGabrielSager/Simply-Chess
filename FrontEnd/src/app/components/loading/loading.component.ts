import {Component, inject, Input, OnInit} from '@angular/core';
import {SessionService} from "../../services/session.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.css',
  standalone: true
})
export class LoadingComponent {
  @Input() title: string = "";
}

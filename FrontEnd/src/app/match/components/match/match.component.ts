import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {map, Subscription} from "rxjs";

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css']
})
export class MatchComponent implements OnInit{
  private route: ActivatedRoute = inject(ActivatedRoute);
  private subs : Subscription = new Subscription();
  matchId: String = "";
  ngOnInit(): void {
    this.route.params.subscribe(
      (params: Params) => {
        this.matchId = params['id'];
      }
    )
  }
}

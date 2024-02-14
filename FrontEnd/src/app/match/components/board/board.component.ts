import { Component } from '@angular/core';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent {
  rows = [1, 2, 3, 4, 5, 6, 7, 8];
  columns = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
}

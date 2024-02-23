import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChange, SimpleChanges} from '@angular/core';
import {Piece} from "../../../models/piece";
import {Pair} from "../../../models/pair";
import {PieceMovement} from "../../../models/piece-movement";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit,OnChanges {
  rows: number[] = [1, 2, 3, 4, 5, 6, 7, 8];
  columns: string[] = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
  symbolToPieceIcon: Map<string, string> = new Map<string, string>([
    ["K", "♚"],
    ["Q", "♛"],
    ["R", "♜"],
    ["B", "♝"],
    ["N", "♞"],
    ["P", "♙︎"]
  ]);
  @Input() pieces: Array<Piece> | undefined = new Array<Piece>();
  @Input() rivalPieces: Array<Piece> | undefined = new Array<Piece>();
  @Output() movePieceEvent: EventEmitter<PieceMovement> = new EventEmitter<PieceMovement>();
  selectedSquare: HTMLSpanElement | null = null;
  selectedPiece: HTMLSpanElement | null = null;

  constructor() {

  }

  ngOnInit(): void {
      if(this.pieces?.at(0)?.color == "WHITE") {
        this.rows = this.rows.reverse();
      }
  }

  // TODO: See if it is necessary to implement this to update the board state.
  ngOnChanges(changes: SimpleChanges): void {
    for (const propName in changes) {
      const change: SimpleChange = changes[propName];
      for (let cp of change.currentValue as Array<Piece>) {
        for (let pp in change.previousValue as Array<Piece>) {

        }
      }
    }
  }

  onSelectedPiece(piece: HTMLSpanElement) {
    this.selectedPiece = piece;
  }


  onSelectedSquare(square: HTMLDivElement) {
    if (this.selectedPiece != null && square.querySelector('span') == null) {
      const pieceMovement: PieceMovement = {
        pieceId: this.selectedPiece.id,
        target: {
          x: Number(square.id.at(0)),
          y: Number(square.id.at(1))
        } as Pair
      } as PieceMovement
      console.log(pieceMovement)
      this.movePieceEvent.emit(pieceMovement);

      this.selectedPiece = null;
      this.selectedSquare = null;
    }
  }
}

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChange, SimpleChanges} from '@angular/core';
import {Piece} from "../../models/piece";
import {Pair} from "../../models/pair";
import {PieceMovement} from "../../models/piece-movement";
import {PieceRequest} from "../../models/piece-request";

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
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
  @Output() onPieceMove: EventEmitter<PieceMovement> = new EventEmitter<PieceMovement>();
  selectedSquare: HTMLSpanElement | null = null;
  selectedPiece: HTMLSpanElement | null = null;

  constructor() {

  }

  ngOnInit(): void {
      if(this.pieces?.at(0)?.color == "WHITE") {
        this.rows = this.rows.reverse();
      }
  }

  onSelectedPiece(piece: HTMLSpanElement) {
    if(this.pieces?.some((p: Piece) => p.id == Number(piece.id))) {
      this.selectedPiece = piece;
    }
    else {
      this.selectedPiece = null;
    }
  }


  onSelectedSquare(square: HTMLDivElement) {
    if (this.selectedPiece != null) {
      const pieceOnSquare: HTMLSpanElement | null = square.querySelector('span');
      if(pieceOnSquare == null)
        this.emitMove(square);
      else {
        if(this.rivalPieces?.some((p: Piece) => p.id == Number(pieceOnSquare.id))) {
          this.emitMove(square);
        }
      }
    }
  }

  private emitMove(pieceSquare: HTMLDivElement) {
    let pieceId: number = Number(this.selectedPiece?.id);
    let pieceToMoveRequest: PieceRequest = {
      color: "BLACK",
      position: {} as Pair
    } as PieceRequest;

    this.pieces?.every((p) => {
      if(this.selectedPiece != null ) {
        if(p.id == pieceId) {
          pieceToMoveRequest.color = p.color;
          pieceToMoveRequest.position = p.position;
          return false;
        }
      }
      return true;
    });

    const pieceMovement: PieceMovement = {
      pieceId: pieceId,
      pieceToMove: pieceToMoveRequest,
      target: {
        x: Number(pieceSquare.id.at(1)),
        y: Number(pieceSquare.id.at(0))
      } as Pair
    } as PieceMovement
    this.onPieceMove.emit(pieceMovement);

    this.selectedPiece = null;
    this.selectedSquare = null;
  }
}

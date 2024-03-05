import {Pair} from "./pair";
import {PieceRequest} from "./piece-request";

export interface PieceMovement {
  pieceId: number;
  pieceToMove: PieceRequest
  target: Pair;
}

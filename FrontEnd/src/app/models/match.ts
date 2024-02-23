import {Piece} from "./piece";

export interface Match {
  id: string;
  status: string;
  isWhiteTurn: boolean;
  whitePieces: Array<Piece>;
  blackPieces: Array<Piece>;
  whitePlayer: string;
  blackPlayer: string;
  winner: string;
  isPromotedPawn: boolean;
}

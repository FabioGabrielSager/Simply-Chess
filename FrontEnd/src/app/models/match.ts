import {Piece} from "./piece";
import {GameStatus} from "./game-status";

export interface Match {
  id: string;
  status: GameStatus;
  finishReason: string;
  isWhiteTurn: boolean;
  whitePieces: Array<Piece>;
  blackPieces: Array<Piece>;
  whitePlayer: string;
  blackPlayer: string;
  winner: string;
  isPromotedPawn: boolean;
}

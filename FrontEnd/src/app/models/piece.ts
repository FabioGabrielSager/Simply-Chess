import {Pair} from "./pair";

export interface Piece {
  id: number;
  position: Pair;
  color: string;
  isAlive: boolean;
  type: string;
}

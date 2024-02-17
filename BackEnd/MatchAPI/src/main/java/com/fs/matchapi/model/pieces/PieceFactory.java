package com.fs.matchapi.model.pieces;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import com.fs.matchapi.model.pieces.common.PieceColor;

public class PieceFactory {
    public static Piece create(char pieceAlgebraicName, PieceColor color, Pair position) {
        switch (Character.toUpperCase(pieceAlgebraicName)) {
            case 'K':
                return King.builder().isAlive(true).color(color).position(position).build();
            case 'Q':
                return Queen.builder().isAlive(true).color(color).position(position).build();
            case 'R':
                return Rook.builder().isAlive(true).color(color).position(position).build();
            case 'B':
                return Bishop.builder().isAlive(true).color(color).position(position).build();
            case 'N':
                return Knight.builder().isAlive(true).color(color).position(position).build();
            case 'P':
                return Pawn.builder().isAlive(true).color(color).position(position).build();
            default:
                throw new IllegalArgumentException(
                        String.format("\"%s\" is an invalid algebraic name", pieceAlgebraicName));
        }
    }

    public static Piece create(char pieceAlgebraicName, PieceColor color) {
        switch (Character.toUpperCase(pieceAlgebraicName)) {
            case 'K':
                return King.builder().isAlive(true).color(color).build();
            case 'Q':
                return Queen.builder().isAlive(true).color(color).build();
            case 'R':
                return Rook.builder().isAlive(true).color(color).build();
            case 'B':
                return Bishop.builder().isAlive(true).color(color).build();
            case 'N':
                return Knight.builder().isAlive(true).color(color).build();
            case 'P':
                return Pawn.builder().isAlive(true).color(color).build();
            default:
                throw new IllegalArgumentException(
                        String.format("\"%s\" is an invalid algebraic name", pieceAlgebraicName));
        }
    }
}

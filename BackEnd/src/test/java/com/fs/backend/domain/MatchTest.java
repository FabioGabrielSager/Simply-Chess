package com.fs.backend.domain;

import com.fs.backend.domain.pieces.Knight;
import com.fs.backend.domain.pieces.Pawn;
import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.exceptions.FinishedGameException;
import com.fs.backend.exceptions.IllegalMovementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchTest {

    @Test
    @Tag("createMatch")
    public void createMatchTest1() {
        Match newMatch = new Match("Jugador1", "Jugador2");

        assertEquals(16, newMatch.getBlackPieces().size());
        assertEquals(16, newMatch.getWhitePieces().size());
    }

    @Test
    @Tag("moveAPiece")
    @DisplayName("Move in wrong turn")
    public void moveTest1() {
        Match newMatch = new Match("", "");
        Optional<Piece> pieceToMove = newMatch.getBlackPieces().stream().filter(p -> p instanceof Pawn &&
                p.getPosition().getX() == 1).findFirst();

        assertThrows(IllegalMovementException.class, () -> {
                    if(pieceToMove.isPresent())
                        newMatch.move(pieceToMove.get(), new Pair(1, 6));
                }
        );

        try {
            if(pieceToMove.isPresent())
                newMatch.move(pieceToMove.get(), new Pair(1, 6));
        } catch (Exception err) {
            assertEquals("You are trying to move a black piece but it's white's turn", err.getMessage());
        }
    }

    @Test
    @Tag("moveAPiece")
    @DisplayName("Move in a finished game")
    public void moveTest2() {
        Match newMatch = new Match("", "");
        newMatch.setWhiteTurn(false);
        newMatch.setFinished(true);
        Optional<Piece> pieceToMove = newMatch.getBlackPieces().stream().filter(p -> p instanceof Pawn &&
                p.getPosition().getX() == 1).findFirst();

        assertThrows(FinishedGameException.class, () -> {
                    if(pieceToMove.isPresent())
                        newMatch.move(pieceToMove.get(), new Pair(1, 6));
                }
        );

        try {
            if(pieceToMove.isPresent())
                newMatch.move(pieceToMove.get(), new Pair(1, 6));
        } catch (Exception err) {
            assertEquals("Cannot make a move in a finished game", err.getMessage());
        }
    }

    @Test
    @Tag("moveAPiece")
    @DisplayName("ValidMoves")
    public void moveTest3() throws IllegalMovementException {
        Match newMatch = new Match("", "");
        Optional<Piece> whitePawnToMoveOptional = newMatch.getWhitePieces().stream().filter(p -> p instanceof Pawn &&
                p.getPosition().getX() == 1).findFirst();
        Optional<Piece> blackPawnToMoveOptional = newMatch.getBlackPieces().stream().filter(p -> p instanceof Pawn &&
                p.getPosition().getX() == 1).findFirst();

        if(whitePawnToMoveOptional.isPresent())
            newMatch.move(whitePawnToMoveOptional.get(), new Pair(1, 3));
        if(blackPawnToMoveOptional.isPresent())
            newMatch.move(blackPawnToMoveOptional.get(), new Pair(1, 5));
    }

    @Test
    @Tag("verifyCheckmate")
    public void verifyCheckmateTes1() {
        Match newMatch = new Match("", "");
        newMatch.verifyCheckmate();

        assertNull(newMatch.getWinner());
        assertFalse(newMatch.isFinished());
    }

    @Test
    @Tag("verifyCheckmate")
    @DisplayName("Black check mate")
    public void verifyCheckmateTest2() {
        Match newMatch = new Match("", "");
        newMatch.getBlackPieces()
                .stream()
                .filter(p -> p instanceof Knight).findFirst()
                .ifPresent(piece -> piece.setPosition(new Pair(4, 3)));


        newMatch.verifyCheckmate();

        assertNotNull(newMatch.getWinner());
        assertEquals(newMatch.getBlackPlayer(), newMatch.getWinner());
        assertTrue(newMatch.isFinished());
    }

    @Test
    @Tag("verifyCheckmate")
    @DisplayName("White check mate")
    public void verifyCheckmateTest3() {
        Match newMatch = new Match("", "");
        newMatch.getWhitePieces()
                .stream()
                .filter(p -> p instanceof Knight).findFirst()
                .ifPresent(piece -> piece.setPosition(new Pair(4, 6 )));


        newMatch.verifyCheckmate();

        assertNotNull(newMatch.getWinner());
        assertEquals(newMatch.getWhitePlayer(), newMatch.getWinner());
        assertTrue(newMatch.isFinished());
    }
}

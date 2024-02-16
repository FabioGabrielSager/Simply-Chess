package com.fs.backend.model.pieces;


import com.fs.backend.model.pieces.common.Pair;
import com.fs.backend.model.pieces.common.PieceColor;
import com.fs.backend.exceptions.PieceBlockingException;
import com.fs.backend.exceptions.DeadPieceMoveException;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.OutOfBoundsException;
import com.fs.backend.exceptions.SamePositionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PawnTest {
    private final int BOARD_LENGTH = 8;
    private Pawn blackPawn;
    private Pawn whitePawn;

    @BeforeEach
    public void setVariables() {
        blackPawn = Pawn.builder()
                .id(1L)
                .position(new Pair(4, 6))
                .color(PieceColor.BLACK)
                .isAlive(true)
                .wasMoved(true)
                .build();

        whitePawn = Pawn.builder()
                .id(1L)
                .position(new Pair(2, 2))
                .color(PieceColor.WHITE)
                .isAlive(true)
                .wasMoved(false)
                .build();
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Moved piece - Pawn's rules violation")
    public void moveTest1() {

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(4, 4), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(3, 6), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(5, 6), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(3, 7), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(5, 7), BOARD_LENGTH, List.of(), List.of()));

        assertEquals(4, blackPawn.getPosition().getX());
        assertEquals(6, blackPawn.getPosition().getY());

        try {
            blackPawn.move(new Pair(5, 7), BOARD_LENGTH, List.of(), List.of());
        } catch (Exception err) {
            assertEquals("The move violates the Pawn's movement rules", err.getMessage());
        }

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Not moved piece - Pawn's rules violation")
    public void moveTest2() {

        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(1, 2), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(3, 2), BOARD_LENGTH, List.of(), List.of()));


        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(1, 1), BOARD_LENGTH, List.of(), List.of()));


        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(2, 1), BOARD_LENGTH, List.of(), List.of()));


        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(3, 1), BOARD_LENGTH, List.of(), List.of()));

        assertEquals(2, whitePawn.getPosition().getX());
        assertEquals(2, whitePawn.getPosition().getY());

        try {
            whitePawn.move(new Pair(3, 1), BOARD_LENGTH, List.of(), List.of());
        } catch (Exception err) {
            assertEquals("The move violates the Pawn's movement rules", err.getMessage());
        }

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Not moved piece - Two steps movement")
    public void moveTest3() {
        blackPawn.setPosition(new Pair(1, 7));
        whitePawn.setPosition(new Pair(1, 2));
        blackPawn.setWasMoved(false);
        whitePawn.setWasMoved(false);
        Pair blackPieceMove = new Pair(1, 5);
        Pair whitePieceMove = new Pair(1, 4);

        blackPawn.move(blackPieceMove, BOARD_LENGTH, List.of(), List.of());

        assertEquals(blackPieceMove.getX(), blackPawn.getPosition().getX());
        assertEquals(blackPieceMove.getY(), blackPawn.getPosition().getY());

        whitePawn.move(whitePieceMove, BOARD_LENGTH, List.of(), List.of());

        assertEquals(whitePieceMove.getX(), whitePawn.getPosition().getX());
        assertEquals(whitePieceMove.getY(), whitePawn.getPosition().getY());
    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Move towards an empty space")
    public void moveTest4() {

        Pair blackPieceMove = new Pair(4, 5);
        Pair whitePieceMove = new Pair(2, 3);

        blackPawn.move(blackPieceMove, BOARD_LENGTH, List.of(), List.of());

        assertEquals(blackPieceMove.getX(), blackPawn.getPosition().getX());
        assertEquals(blackPieceMove.getY(), blackPawn.getPosition().getY());

        whitePawn.move(whitePieceMove, BOARD_LENGTH, List.of(), List.of());

        assertEquals(whitePieceMove.getX(), whitePawn.getPosition().getX());
        assertEquals(whitePieceMove.getY(), whitePawn.getPosition().getY());
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Attack on empty space")
    public void moveTest5() {

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(5, 5), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                blackPawn.move(new Pair(3, 5), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(1, 3), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                whitePawn.move(new Pair(3, 3), BOARD_LENGTH, List.of(), List.of()));

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Move a dead piece")
    public void moveTest6() {

        blackPawn.setAlive(false);
        whitePawn.setAlive(false);

        assertThrows(DeadPieceMoveException.class, () ->
                blackPawn.move(new Pair(4, 5), BOARD_LENGTH, List.of(), List.of())
        );

        assertThrows(DeadPieceMoveException.class, () ->
                whitePawn.move(new Pair(2, 4), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards the same position")
    public void moveTest7() {

        assertThrows(SamePositionException.class, () ->
                blackPawn.move(new Pair(4, 6), BOARD_LENGTH, List.of(), List.of())
        );

        assertThrows(SamePositionException.class, () ->
                whitePawn.move(new Pair(2, 2), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement outside the boundaries of the board")
    public void moveTest8() {

        blackPawn.setPosition(new Pair(4, 1));
        whitePawn.setPosition(new Pair(2, 8));

        assertThrows(OutOfBoundsException.class, () ->
                blackPawn.move(new Pair(4, 0), BOARD_LENGTH, List.of(), List.of())
        );

        assertThrows(OutOfBoundsException.class, () ->
                whitePawn.move(new Pair(2, 9), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards an allied position")
    public void moveTest9() {
        Pawn alliedPiece1 = Pawn.builder()
                .position(new Pair(4, 5))
                .build();

        Pawn alliedPiece2 = Pawn.builder()
                .position(new Pair(2, 3))
                .build();

        assertThrows(PieceBlockingException.class, () ->
                blackPawn.move(new Pair(4, 5), BOARD_LENGTH, List.of(alliedPiece1), List.of())
        );

        assertThrows(PieceBlockingException.class, () ->
                whitePawn.move(new Pair(2, 3), BOARD_LENGTH, List.of(alliedPiece2), List.of())
        );

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Attack on enemy position")
    public void moveTest10() {
        Pawn alliedPiece1 = Pawn.builder()
                .position(new Pair(5, 5))
                .build();

        Pawn alliedPiece2 = Pawn.builder()
                .position(new Pair(1, 3))
                .build();

        blackPawn.move(new Pair(5, 5), BOARD_LENGTH, List.of(), List.of(alliedPiece1));

        assertEquals(5, blackPawn.getPosition().getX());
        assertEquals(5, blackPawn.getPosition().getY());

        blackPawn.setPosition(new Pair(4, 6));
        alliedPiece1.setPosition(new Pair(3, 5));
        blackPawn.move(new Pair(3, 5), BOARD_LENGTH, List.of(), List.of(alliedPiece1));

        assertEquals(3, blackPawn.getPosition().getX());
        assertEquals(5, blackPawn.getPosition().getY());

        whitePawn.move(new Pair(1, 3), BOARD_LENGTH, List.of(), List.of(alliedPiece2));

        assertEquals(1, whitePawn.getPosition().getX());
        assertEquals(3, whitePawn.getPosition().getY());

        whitePawn.setPosition(new Pair(2, 2));
        alliedPiece2.setPosition(new Pair(3, 3));
        whitePawn.move(new Pair(3, 3), BOARD_LENGTH, List.of(), List.of(alliedPiece2));

        assertEquals(3, whitePawn.getPosition().getX());
        assertEquals(3, whitePawn.getPosition().getY());
    }

    @Test
    @Tag("isPromote")
    @DisplayName("Pawns are in promotion position")
    public void isPromotedTest1() {
        whitePawn.setPosition(new Pair(1, 8));
        blackPawn.setPosition(new Pair(1,   1));

        assertTrue(whitePawn.isPromoted(BOARD_LENGTH));
        assertTrue(blackPawn.isPromoted(BOARD_LENGTH));
    }

    @Test
    @Tag("isPromote")
    @DisplayName("Pawns are not in promotion position")
    public void isPromotedTest2() {
        assertFalse(whitePawn.isPromoted(BOARD_LENGTH));
        assertFalse(blackPawn.isPromoted(BOARD_LENGTH));
    }
}

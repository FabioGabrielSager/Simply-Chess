package com.fs.backend.model.pieces;

import com.fs.backend.model.pieces.common.Pair;
import com.fs.backend.model.pieces.common.PieceColor;
import com.fs.backend.exceptions.DeadPieceMoveException;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.OutOfBoundsException;
import com.fs.backend.exceptions.PieceBlockingException;
import com.fs.backend.exceptions.SamePositionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RookTest {
    private final int BOARD_LENGTH = 8;
    private Rook rook;
    private King alliedKing;

    @BeforeEach
    public void setVariables() {
        rook = Rook.builder()
                .position(new Pair(4, 6))
                .color(PieceColor.BLACK)
                .wasMoved(true)
                .isAlive(true)
                .build();

     alliedKing = King.builder()
                .position(new Pair(5, 8))
                .color(PieceColor.BLACK)
                .isAlive(true)
                .build();

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Rook's rules violation")
    public void moveTest1() {

        assertThrows(IllegalMovementException.class, () ->
                rook.move(new Pair(6, 4), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                rook.move(new Pair(5, 7), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                rook.move(new Pair(3, 7), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                rook.move(new Pair(2, 4), BOARD_LENGTH, List.of(), List.of()));

        assertEquals(4, rook.getPosition().getX());
        assertEquals(6, rook.getPosition().getY());

        try {
            rook.move(new Pair(2, 4), BOARD_LENGTH, List.of(), List.of());
        } catch (Exception err) {
            assertEquals("The move violates the Rook's movement rules", err.getMessage());
        }

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Move towards an empty space")
    public void moveTest2() {

        Pair movement = new Pair(1 ,6);

        rook.move(movement, BOARD_LENGTH, List.of(alliedKing), List.of());

        assertEquals(movement.getX(), rook.getPosition().getX());
        assertEquals(movement.getY(), rook.getPosition().getY());

        setVariables();
        movement = new Pair(8, 6);

        rook.move(movement, BOARD_LENGTH, List.of(alliedKing), List.of());

        assertEquals(movement.getX(), rook.getPosition().getX());
        assertEquals(movement.getY(), rook.getPosition().getY());

        setVariables();
        movement = new Pair(4, 5);

        rook.move(movement, BOARD_LENGTH, List.of(alliedKing), List.of());

        assertEquals(movement.getX(), rook.getPosition().getX());
        assertEquals(movement.getY(), rook.getPosition().getY());

        setVariables();
        movement = new Pair(4, 7);

        rook.move(movement, BOARD_LENGTH, List.of(alliedKing), List.of());

        assertEquals(movement.getX(), rook.getPosition().getX());
        assertEquals(movement.getY(), rook.getPosition().getY());


    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Move a dead piece")
    public void moveTest3() {

        rook.setAlive(false);

        assertThrows(DeadPieceMoveException.class, () ->
                rook.move(new Pair(4, 8), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards the same position")
    public void moveTest4() {

        assertThrows(SamePositionException.class, () ->
                rook.move(new Pair(4, 6), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement outside the boundaries of the board")
    public void moveTest5() {

        assertThrows(OutOfBoundsException.class, () ->
                rook.move(new Pair(4, 9), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards an allied position")
    public void moveTest6() {
        Bishop alliedPiece = Bishop.builder()
                .position(new Pair(4, 1))
                .build();

        assertThrows(PieceBlockingException.class, () ->
                rook.move(new Pair(4, 1), BOARD_LENGTH, List.of(alliedPiece), List.of())
        );

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Attack on enemy position")
    public void moveTest7() {
        Bishop enemyPiece = Bishop.builder()
                .position(new Pair(4, 7))
                .build();

        rook.move(new Pair(4, 7), BOARD_LENGTH, List.of(alliedKing), List.of(enemyPiece));

        assertEquals(4, rook.getPosition().getX());
        assertEquals(7, rook.getPosition().getY());

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement through an allied position")
    public void moveTest8() {
        Bishop alliedPiece = Bishop.builder()
                .position(new Pair(7, 6))
                .isAlive(true)
                .build();

        assertThrows(PieceBlockingException.class, () ->
                rook.move(new Pair(8, 6), BOARD_LENGTH, List.of(alliedPiece), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement through an enemy position")
    public void moveTest9() {
        Bishop enemyPiece = Bishop.builder()
                .position(new Pair(3, 6))
                .isAlive(true)
                .build();

        assertThrows(PieceBlockingException.class, () ->
                rook.move(new Pair(1, 6), BOARD_LENGTH, List.of(alliedKing), List.of(enemyPiece))
        );
    }
}

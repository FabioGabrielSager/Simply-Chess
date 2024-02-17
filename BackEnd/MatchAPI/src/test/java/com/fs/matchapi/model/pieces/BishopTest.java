package com.fs.matchapi.model.pieces;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.PieceColor;
import com.fs.matchapi.exceptions.PieceBlockingException;
import com.fs.matchapi.exceptions.DeadPieceMoveException;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.exceptions.OutOfBoundsException;
import com.fs.matchapi.exceptions.SamePositionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BishopTest {
    private final int BOARD_LENGTH = 8;
    private Bishop blackSquareBishop;
    private Bishop whiteSquareBishop;

    @BeforeEach
    public void setVariables() {
        blackSquareBishop = Bishop.builder()
                .position(new Pair(4, 6))
                .color(PieceColor.BLACK)
                .isAlive(true)
                .build();

        whiteSquareBishop = Bishop.builder()
                .position(new Pair(6, 3))
                .color(PieceColor.BLACK)
                .isAlive(true)
                .build();

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Bishop's rules violation")
    public void moveTest1() {

        assertThrows(IllegalMovementException.class, () ->
                blackSquareBishop.move(new Pair(5, 4), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                blackSquareBishop.move(new Pair(1, 6), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                whiteSquareBishop.move(new Pair(7, 1), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                whiteSquareBishop.move(new Pair(6, 5), BOARD_LENGTH, List.of(), List.of()));

        assertEquals(4, blackSquareBishop.getPosition().getX());
        assertEquals(6, blackSquareBishop.getPosition().getY());

        assertEquals(6, whiteSquareBishop.getPosition().getX());
        assertEquals(3, whiteSquareBishop.getPosition().getY());

        try {
            blackSquareBishop.move(new Pair(5, 4), BOARD_LENGTH, List.of(), List.of());
        } catch (Exception err) {
            assertEquals("The move violates the Bishop's movement rules", err.getMessage());
        }

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Move towards an empty space")
    public void moveTest2() {

        Pair movement = new Pair(2 ,4);

        blackSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), blackSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), blackSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(7, 3);

        blackSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), blackSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), blackSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(6, 8);

        blackSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), blackSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), blackSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(2, 8);

        blackSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), blackSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), blackSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(7, 2);

        whiteSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), whiteSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), whiteSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(4, 1);

        whiteSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), whiteSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), whiteSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(2, 7);

        whiteSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), whiteSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), whiteSquareBishop.getPosition().getY());

        setVariables();
        movement = new Pair(8, 5);

        whiteSquareBishop.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), whiteSquareBishop.getPosition().getX());
        assertEquals(movement.getY(), whiteSquareBishop.getPosition().getY());

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Move a dead piece")
    public void moveTest3() {

        blackSquareBishop.setAlive(false);

        assertThrows(DeadPieceMoveException.class, () ->
                blackSquareBishop.move(new Pair(6, 4), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards the same position")
    public void moveTest4() {

        assertThrows(SamePositionException.class, () ->
                blackSquareBishop.move(new Pair(4, 6), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement outside the boundaries of the board")
    public void moveTest5() {

        assertThrows(OutOfBoundsException.class, () ->
                blackSquareBishop.move(new Pair(9, 1), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards an allied position")
    public void moveTest6() {
        Bishop alliedPiece = Bishop.builder()
                .position(new Pair(8, 2))
                .build();

        assertThrows(PieceBlockingException.class, () ->
                blackSquareBishop.move(new Pair(8, 2), BOARD_LENGTH, List.of(alliedPiece), List.of())
        );

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Attack on enemy position")
    public void moveTest7() {
        Bishop enemyPiece = Bishop.builder()
                .position(new Pair(8, 2))
                .build();

        blackSquareBishop.move(new Pair(8, 2), BOARD_LENGTH, List.of(), List.of(enemyPiece));

        assertEquals(8, blackSquareBishop.getPosition().getX());
        assertEquals(2, blackSquareBishop.getPosition().getY());

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement through an allied position")
    public void moveTest8() {
        Bishop alliedPiece = Bishop.builder()
                .position(new Pair(6, 4))
                .isAlive(true)
                .build();

        assertThrows(PieceBlockingException.class, () ->
                blackSquareBishop.move(new Pair(8, 2), BOARD_LENGTH, List.of(alliedPiece), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement through an enemy position")
    public void moveTest9() {
        Bishop enemyPiece = Bishop.builder()
                .position(new Pair(6, 4))
                .isAlive(true)
                .build();

        assertThrows(PieceBlockingException.class, () ->
                blackSquareBishop.move(new Pair(8, 2), BOARD_LENGTH, List.of(), List.of(enemyPiece))
        );
    }
}

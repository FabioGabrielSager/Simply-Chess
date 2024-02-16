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

public class QueenTest {
    private final int BOARD_LENGTH = 8;
    private Queen queen;

    @BeforeEach
    public void setVariables() {
        queen = Queen.builder()
                .position(new Pair(4, 6))
                .color(PieceColor.BLACK)
                .isAlive(true)
                .build();

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Bishop's rules violation")
    public void moveTest1() {

        assertThrows(IllegalMovementException.class, () ->
                queen.move(new Pair(5, 3), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                queen.move(new Pair(1, 5), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                queen.move(new Pair(2, 7), BOARD_LENGTH, List.of(), List.of()));


        assertThrows(IllegalMovementException.class, () ->
                queen.move(new Pair(6, 7), BOARD_LENGTH, List.of(), List.of()));


        assertEquals(4, queen.getPosition().getX());
        assertEquals(6, queen.getPosition().getY());

        try {
            queen.move(new Pair(5, 4), BOARD_LENGTH, List.of(), List.of());
        } catch (Exception err) {
            assertEquals("The move violates the Queen's movement rules", err.getMessage());
        }

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Move towards an empty space")
    public void moveTest2() {

        Pair movement = new Pair(2 ,4);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(7, 3);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(6, 8);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(2, 8);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(1 ,6);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(8, 6);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(4, 5);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());

        setVariables();
        movement = new Pair(4, 7);

        queen.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), queen.getPosition().getX());
        assertEquals(movement.getY(), queen.getPosition().getY());


    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Move a dead piece")
    public void moveTest3() {

        queen.setAlive(false);

        assertThrows(DeadPieceMoveException.class, () ->
                queen.move(new Pair(6, 4), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards the same position")
    public void moveTest4() {

        assertThrows(SamePositionException.class, () ->
                queen.move(new Pair(4, 6), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement outside the boundaries of the board")
    public void moveTest5() {

        assertThrows(OutOfBoundsException.class, () ->
                queen.move(new Pair(9, 1), BOARD_LENGTH, List.of(), List.of())
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
                queen.move(new Pair(8, 2), BOARD_LENGTH, List.of(alliedPiece), List.of())
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

        queen.move(new Pair(8, 2), BOARD_LENGTH, List.of(), List.of(enemyPiece));

        assertEquals(8, queen.getPosition().getX());
        assertEquals(2, queen.getPosition().getY());

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
                queen.move(new Pair(8, 2), BOARD_LENGTH, List.of(alliedPiece), List.of())
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
                queen.move(new Pair(8, 2), BOARD_LENGTH, List.of(), List.of(enemyPiece))
        );
    }
}

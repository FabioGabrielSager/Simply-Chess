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

public class KnightTest {
    private final int BOARD_LENGTH = 8;
    private Knight knight;

    @BeforeEach
    public void setVariables() {
        knight = Knight.builder()
                .position(new Pair(4, 6))
                .color(PieceColor.BLACK)
                .isAlive(true)
                .build();

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Knight's rules violation")
    public void moveTest1() {

        assertThrows(IllegalMovementException.class, () ->
                knight.move(new Pair(5, 6), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                knight.move(new Pair(4, 4), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                knight.move(new Pair(6, 4), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () ->
                knight.move(new Pair(2, 6), BOARD_LENGTH, List.of(), List.of()));

        assertThrows(IllegalMovementException.class, () -> knight.move(new Pair(4, 6),
                BOARD_LENGTH, List.of(), List.of()));

        assertEquals(4, knight.getPosition().getX());
        assertEquals(6, knight.getPosition().getY());

        try {
            knight.move(new Pair(2, 6), BOARD_LENGTH, List.of(), List.of());
        } catch (Exception err) {
            assertEquals("The move violates the Knight's movement rules", err.getMessage());
        }

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Move towards an empty space")
    public void moveTest2() {

        Pair movement = new Pair(3 ,4);

        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(5, 4);

        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(6, 5);

        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(6, 7);

        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(5, 8);

        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(3, 8);


        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(2, 7);

        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

        setVariables();
        movement = new Pair(2, 5);


        knight.move(movement, BOARD_LENGTH, List.of(), List.of());

        assertEquals(movement.getX(), knight.getPosition().getX());
        assertEquals(movement.getY(), knight.getPosition().getY());

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Move a dead piece")
    public void moveTest3() {

        knight.setAlive(false);

        assertThrows(DeadPieceMoveException.class, () ->
                knight.move(new Pair(4, 5), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards the same position")
    public void moveTest4() {

        assertThrows(SamePositionException.class, () ->
                knight.move(new Pair(4, 6), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement outside the boundaries of the board")
    public void moveTest5() {

        knight.setPosition(new Pair(4, 1));

        assertThrows(OutOfBoundsException.class, () ->
                knight.move(new Pair(7, 0), BOARD_LENGTH, List.of(), List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards an allied position")
    public void moveTest6() {
        Knight alliedPiece = Knight.builder()
                .position(new Pair(3, 4))
                .build();

        assertThrows(PieceBlockingException.class, () ->
                knight.move(new Pair(3, 4), BOARD_LENGTH, List.of(alliedPiece), List.of())
        );

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Attack on enemy position")
    public void moveTest7() {
        Knight enemyPiece = Knight.builder()
                .position(new Pair(3, 4))
                .build();

        knight.move(new Pair(3, 4), BOARD_LENGTH, List.of(), List.of(enemyPiece));

        assertEquals(3, knight.getPosition().getX());
        assertEquals(4, knight.getPosition().getY());

    }
}

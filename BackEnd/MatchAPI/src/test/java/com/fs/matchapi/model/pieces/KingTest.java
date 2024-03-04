package com.fs.matchapi.model.pieces;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KingTest {
    private final int BOARD_LENGTH = 8;
    private King king;
    private Rook kingSideRook;
    private Rook queenSideRook;
    private List<Piece> allies;

    @BeforeEach
    public void setVariables() {
        king = King.builder()
                .position(new Pair(4, 6))
                .color(PieceColor.BLACK)
                .wasMoved(true)
                .isAlive(true)
                .build();

        kingSideRook = queenSideRook.builder()
                .position(new Pair(8, 8))
                .color(PieceColor.BLACK)
                .wasMoved(false)
                .isAlive(true)
                .build();

        queenSideRook = Rook.builder()
                .position(new Pair(1, 8))
                .color(PieceColor.BLACK)
                .wasMoved(false)
                .isAlive(true)
                .build();

        allies = new ArrayList<Piece>(Arrays.asList(queenSideRook, kingSideRook));
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("King's rules violation")
    public void moveTest1() {

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(6, 4), BOARD_LENGTH, allies, List.of()));

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(6, 6), BOARD_LENGTH, allies, List.of()));

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(1, 6), BOARD_LENGTH, allies, List.of()));

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(1, 8), BOARD_LENGTH, allies, List.of()));

        assertEquals(4, king.getPosition().getX());
        assertEquals(6, king.getPosition().getY());

        try {
            king.move(new Pair(1, 8), BOARD_LENGTH, allies, List.of());
        } catch (Exception err) {
            assertEquals("The move violates the King's movement rules", err.getMessage());
        }

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Move towards an empty space")
    public void moveTest2() {

        Pair movement = new Pair(3, 5);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(4, 5);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(5, 5);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(5, 6);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(5, 7);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(4, 7);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(3, 7);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());

        setVariables();
        movement = new Pair(3, 6);

        king.move(movement, BOARD_LENGTH, allies, List.of());

        assertEquals(movement.getX(), king.getPosition().getX());
        assertEquals(movement.getY(), king.getPosition().getY());


    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Move a dead piece")
    public void moveTest3() {

        king.setAlive(false);

        assertThrows(DeadPieceMoveException.class, () ->
                king.move(new Pair(3, 5), BOARD_LENGTH, allies, List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards the same position")
    public void moveTest4() {

        assertThrows(SamePositionException.class, () ->
                king.move(new Pair(4, 6), BOARD_LENGTH, allies, List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement outside the boundaries of the board")
    public void moveTest5() {

        king.setPosition(new Pair(5, 8));

        assertThrows(OutOfBoundsException.class, () ->
                king.move(new Pair(4, 0), BOARD_LENGTH, allies, List.of())
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards an allied position")
    public void moveTest6() {
        Bishop alliedPiece = Bishop.builder()
                .position(new Pair(5, 5))
                .build();

        allies.add(alliedPiece);

        assertThrows(PieceBlockingException.class, () ->
                king.move(new Pair(5, 5), BOARD_LENGTH, allies, List.of())
        );

    }

    @SneakyThrows
    @Test
    @Tag("moveMethod")
    @DisplayName("Attack on enemy position")
    public void moveTest7() {
        Bishop enemyPiece = Bishop.builder()
                .position(new Pair(5, 5))
                .build();

        king.move(new Pair(5, 5), BOARD_LENGTH, allies, List.of(enemyPiece));

        assertEquals(5, king.getPosition().getX());
        assertEquals(5, king.getPosition().getY());

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Movement towards square under attack")
    public void moveTest8() {
        Bishop attacker = Bishop.builder()
                .position(new Pair(3, 3))
                .isAlive(true)
                .build();

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(5, 5), BOARD_LENGTH, allies, List.of(attacker))
        );

    }

    @Test
    @Tag("moveMethod")
    @DisplayName("King side castling")
    public void moveTest9() throws IllegalMovementException {

        king.setPosition(new Pair(5, 8));
        king.setWasMoved(false);

        king.move(new Pair(7, 8), BOARD_LENGTH, allies, List.of());

        assertTrue(kingSideRook.wasMoved());
        assertEquals(6, kingSideRook.getPosition().getX());
        assertEquals(8, kingSideRook.getPosition().getY());

        assertTrue(king.wasMoved());
        assertEquals(7, king.getPosition().getX());
        assertEquals(8, king.getPosition().getY());
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Queen side castling")
    public void moveTest10() throws IllegalMovementException {

        king.setPosition(new Pair(5, 8));
        king.setWasMoved(false);

        king.move(new Pair(3, 8), BOARD_LENGTH, allies, List.of());

        assertTrue(queenSideRook.wasMoved());
        assertEquals(4, queenSideRook.getPosition().getX());
        assertEquals(8, queenSideRook.getPosition().getY());

        assertTrue(king.wasMoved());
        assertEquals(3, king.getPosition().getX());
        assertEquals(8, king.getPosition().getY());
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Illegal castling - rook was moved")
    public void moveTest11() throws IllegalMovementException {

        king.setPosition(new Pair(5, 8));
        king.setWasMoved(false);
        queenSideRook.wasMoved(true);

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(3, 8), BOARD_LENGTH, allies, List.of()));

        assertFalse(king.wasMoved());
        assertEquals(5, king.getPosition().getX());
        assertEquals(8, king.getPosition().getY());
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Illegal castling - king was moved")
    public void moveTest12() throws IllegalMovementException {

        king.setPosition(new Pair(5, 8));
        king.setWasMoved(true);

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(3, 8), BOARD_LENGTH, allies, List.of()));

        assertEquals(5, king.getPosition().getX());
        assertEquals(8, king.getPosition().getY());
    }

    @Test
    @Tag("moveMethod")
    @DisplayName("Illegal castling - king is under attack")
    public void moveTest13() throws IllegalMovementException {
        Rook attaker = Rook.builder()
                .position(new Pair(5, 1))
                .color(PieceColor.WHITE)
                .wasMoved(true)
                .isAlive(true)
                .build();

        king.setPosition(new Pair(5, 8));
        king.setWasMoved(false);

        assertThrows(IllegalMovementException.class, () ->
                king.move(new Pair(3, 8), BOARD_LENGTH, allies, List.of(attaker)));

        assertFalse(king.wasMoved());
        assertEquals(5, king.getPosition().getX());
        assertEquals(8, king.getPosition().getY());
    }

    @Test
    @Tag("isCheckmate")
    @DisplayName("There is check mate")
    public void isCheckmateTest1() {
        king.setPosition(new Pair(8, 8));
        kingSideRook.setPosition(new Pair(1, 1));
        List<Piece> enemies = Arrays.asList(
                Rook.builder()
                        .position(new Pair(5, 8))
                        .color(PieceColor.WHITE)
                        .isAlive(true)
                        .build(),
                Rook.builder()
                        .position(new Pair(8, 4))
                        .color(PieceColor.WHITE)
                        .isAlive(true)
                        .build(),
                Bishop.builder()
                        .position(new Pair(5, 5))
                        .color(PieceColor.WHITE)
                        .isAlive(true)
                        .build()
                );

        assertTrue(king.isCheckmate(BOARD_LENGTH, allies, enemies));
    }


    @Test
    @Tag("isCheckmate")
    @DisplayName("There is no check mate")
    public void isCheckmateTest2() {
        king.setPosition(new Pair(8, 8));
        kingSideRook.setPosition(new Pair(1, 1));
        List<Piece> enemies = Arrays.asList(
                Rook.builder()
                        .position(new Pair(5, 7))
                        .color(PieceColor.WHITE)
                        .isAlive(true)
                        .build(),
                Rook.builder()
                        .position(new Pair(7, 4))
                        .color(PieceColor.WHITE)
                        .isAlive(true)
                        .build(),
                Bishop.builder()
                        .position(new Pair(4, 5))
                        .color(PieceColor.WHITE)
                        .isAlive(true)
                        .build()
        );

        assertFalse(king.isCheckmate(BOARD_LENGTH, allies, enemies));
    }
}

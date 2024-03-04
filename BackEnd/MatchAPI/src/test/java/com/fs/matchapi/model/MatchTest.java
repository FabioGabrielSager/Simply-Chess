package com.fs.matchapi.model;

import com.fs.matchapi.model.pieces.Bishop;
import com.fs.matchapi.model.pieces.Knight;
import com.fs.matchapi.model.pieces.Pawn;
import com.fs.matchapi.model.pieces.PieceFactory;
import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.model.pieces.common.PieceColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchTest {
    private Match inProgressMatch;
    private Match newMatch;
    private Player player;

    @BeforeEach
    public void setUp() {
        player = Player.builder().id(UUID.randomUUID()).build();
        inProgressMatch = new Match(player);
        newMatch = new Match(player);
        if (inProgressMatch.getWhitePlayer() == null) {
            inProgressMatch.setWhitePlayer(Player.builder().build());
        } else {
            inProgressMatch.setBlackPlayer(Player.builder().build());
        }
    }

    @Test
    @Tag("createMatch")
    public void createMatchTest1() {
        Match newMatch = new Match("Jugador1", "Jugador2");

        assertEquals(16, newMatch.getBlackPieces().size());
        assertEquals(16, newMatch.getWhitePieces().size());
    }

    @Test
    @Tag("createMatch")
    public void createMatchTest2() {
        Match newMatch = new Match(player);

        assertEquals(16, newMatch.getBlackPieces().size());
        assertEquals(16, newMatch.getWhitePieces().size());
    }

    @Test
    @Tag("createMatch")
    public void createMatchTest3() {
        Match newMatch = new Match(player, Player.builder().build());

        assertEquals(16, newMatch.getBlackPieces().size());
        assertEquals(16, newMatch.getWhitePieces().size());
    }

    @Test
    @Tag("moveAPiece")
    @DisplayName("ValidMoves")
    public void moveTest3() throws IllegalMovementException {
        inProgressMatch.getBlackPlayer().setId(UUID.randomUUID());
        inProgressMatch.getWhitePlayer().setId(UUID.randomUUID());
        Optional<Piece> whitePawnToMoveOptional = inProgressMatch.getWhitePieces().stream().filter(p -> p instanceof Pawn &&
                p.getPosition().getX() == 1).findFirst();
        Optional<Piece> blackPawnToMoveOptional = inProgressMatch.getBlackPieces().stream().filter(p -> p instanceof Pawn &&
                p.getPosition().getX() == 1).findFirst();

        if (whitePawnToMoveOptional.isPresent())
            inProgressMatch.move(whitePawnToMoveOptional.get(), new Pair(1, 3));
        if (blackPawnToMoveOptional.isPresent())
            inProgressMatch.move(blackPawnToMoveOptional.get(), new Pair(1, 5));
    }

    @Test
    @Tag("verifyCheckmate")
    public void verifyCheckmateTes1() {
        inProgressMatch.verifyCheckmate();

        assertNull(inProgressMatch.getWinner());
        assertNotEquals(MatchStatus.FINISHED_BY_WIN, inProgressMatch.getStatus());
    }

    @Test
    @Tag("verifyCheckmate")
    @DisplayName("Black check mate")
    public void verifyCheckmateTest2() {
        inProgressMatch.getBlackPieces()
                .stream()
                .filter(p -> p instanceof Knight).findFirst()
                .ifPresent(piece -> piece.setPosition(new Pair(4, 3)));
        inProgressMatch.getWhitePieces()
                .stream()
                .filter(p -> p instanceof Pawn
                        && (p.getPosition().getX() == 3 || p.getPosition().getX() == 5)
                        && p.getPosition().getY() == 2 || p instanceof Bishop
                        && p.getPosition().getY() == 1 && p.getPosition().getX() == 6)
                .forEach(p -> {
                    p.setAlive(false);
                    inProgressMatch.getBlackPieces().add(
                            PieceFactory
                                    .create(p.getClass().getSimpleName().charAt(0), PieceColor.BLACK, p.getPosition()));
                });

        inProgressMatch.verifyCheckmate();

        assertNotNull(inProgressMatch.getWinner());
        assertEquals(PieceColor.BLACK, inProgressMatch.getWinner());
        assertEquals(MatchStatus.FINISHED_BY_WIN, inProgressMatch.getStatus());

    }

    @Test
    @Tag("verifyCheckmate")
    @DisplayName("White check mate")
    public void verifyCheckmateTest3() {
        inProgressMatch.setWhitePlayer(Player.builder().id(UUID.randomUUID()).build());
        inProgressMatch.getWhitePieces()
                .stream()
                .filter(p -> p instanceof Knight).findFirst()
                .ifPresent(piece -> piece.setPosition(new Pair(4, 6)));
        inProgressMatch.getBlackPieces()
                .stream()
                .filter(p -> p instanceof Pawn
                        && (p.getPosition().getX() == 3 || p.getPosition().getX() == 5)
                        && p.getPosition().getY() == 7 || p instanceof Bishop
                        && p.getPosition().getY() == 8 && p.getPosition().getX() == 6)
                .forEach(p -> {
                    p.setAlive(false);
                    inProgressMatch.getWhitePieces().add(
                            PieceFactory
                                    .create(p.getClass().getSimpleName().charAt(0), PieceColor.WHITE, p.getPosition()));
                });

        inProgressMatch.verifyCheckmate();

        assertNotNull(inProgressMatch.getWinner());
        assertEquals(PieceColor.WHITE, inProgressMatch.getWinner());
        assertEquals(MatchStatus.FINISHED_BY_WIN, inProgressMatch.getStatus());
    }

    @Test
    @Tag("promoteAPawn")
    @DisplayName("Make a legal promotion")
    public void promoteAPawnTest1() throws IllegalMovementException {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        Pawn promotedPawn = (Pawn) PieceFactory.create('P', PieceColor.BLACK, new Pair(1, 1));

        match.getBlackPieces().add(promotedPawn);

        Piece newPiece = PieceFactory.create('Q', PieceColor.BLACK);

        match.promoteAPawn(promotedPawn, newPiece);

        assertFalse(promotedPawn.isAlive());
        assertTrue(match.getBlackPieces().contains(newPiece));
    }

    @Test
    @Tag("promoteAPawn")
    @DisplayName("Make an ilegal promotion")
    public void promoteAPawnTest2() throws IllegalMovementException {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        Pawn promotedPawn = (Pawn) PieceFactory.create('P', PieceColor.BLACK, new Pair(1, 5));

        match.getBlackPieces().add(promotedPawn);

        Piece newPiece = PieceFactory.create('Q', PieceColor.BLACK);

        assertThrows(IllegalMovementException.class, () -> match.promoteAPawn(promotedPawn, newPiece));
        assertTrue(promotedPawn.isAlive());
        assertFalse(match.getBlackPieces().contains(newPiece));
    }

    @Test
    @Tag("isDeadPosition")
    @DisplayName("king against king")
    public void isADeadPositionTest1() {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        match.setWhitePieces(new ArrayList<>());
        match.getBlackPieces().add(PieceFactory.create('K', PieceColor.BLACK, new Pair(4, 8)));
        match.getWhitePieces().add(PieceFactory.create('K', PieceColor.WHITE, new Pair(4, 1)));

        assertTrue(match.isDeadPosition());
    }

    @Test
    @Tag("isDeadPosition")
    @DisplayName("Black king against white king and bishop")
    public void isADeadPositionTest2() {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        match.setWhitePieces(new ArrayList<>());
        match.getBlackPieces().add(PieceFactory.create('K', PieceColor.BLACK, new Pair(4, 8)));
        match.getWhitePieces().add(PieceFactory.create('K', PieceColor.WHITE, new Pair(4, 1)));
        match.getWhitePieces().add(PieceFactory.create('B', PieceColor.WHITE, new Pair(3, 1)));

        assertTrue(match.isDeadPosition());
    }

    @Test
    @Tag("isDeadPosition")
    @DisplayName("White king against black king and bishop")
    public void isADeadPositionTest3() {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        match.setWhitePieces(new ArrayList<>());
        match.getWhitePieces().add(PieceFactory.create('K', PieceColor.WHITE, new Pair(4, 1)));
        match.getBlackPieces().add(PieceFactory.create('K', PieceColor.BLACK, new Pair(4, 8)));
        match.getBlackPieces().add(PieceFactory.create('B', PieceColor.BLACK, new Pair(3, 8)));

        assertTrue(match.isDeadPosition());
    }

    @Test
    @Tag("isDeadPosition")
    @DisplayName("Black king against white king and knight")
    public void isADeadPositionTest4() {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        match.setWhitePieces(new ArrayList<>());
        match.getBlackPieces().add(PieceFactory.create('K', PieceColor.BLACK, new Pair(4, 8)));
        match.getWhitePieces().add(PieceFactory.create('K', PieceColor.WHITE, new Pair(4, 1)));
        match.getWhitePieces().add(PieceFactory.create('N', PieceColor.WHITE, new Pair(2, 1)));

        assertTrue(match.isDeadPosition());
    }

    @Test
    @Tag("isDeadPosition")
    @DisplayName("White king against black king and knight")
    public void isADeadPositionTest5() {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        match.setWhitePieces(new ArrayList<>());
        match.getWhitePieces().add(PieceFactory.create('K', PieceColor.WHITE, new Pair(4, 1)));
        match.getBlackPieces().add(PieceFactory.create('K', PieceColor.BLACK, new Pair(4, 8)));
        match.getBlackPieces().add(PieceFactory.create('N', PieceColor.BLACK, new Pair(2, 8)));

        assertTrue(match.isDeadPosition());
    }


    @Test
    @Tag("isDeadPosition")
    @DisplayName("Black king and bishop against white king and bishop, with the two bishops in squares of the same color")
    public void isADeadPositionTest6() {
        Match match = new Match();
        match.setBlackPieces(new ArrayList<>());
        match.setWhitePieces(new ArrayList<>());
        match.getWhitePieces().add(PieceFactory.create('K', PieceColor.WHITE, new Pair(4, 1)));
        match.getWhitePieces().add(PieceFactory.create('B', PieceColor.WHITE, new Pair(6, 1)));
        match.getBlackPieces().add(PieceFactory.create('K', PieceColor.BLACK, new Pair(4, 8)));
        match.getBlackPieces().add(PieceFactory.create('B', PieceColor.BLACK, new Pair(7, 8)));

        assertTrue(match.isDeadPosition());
    }
}

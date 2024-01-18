package com.fs.backend.domain;

import com.fs.backend.domain.pieces.King;
import com.fs.backend.domain.pieces.PieceFactory;
import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.domain.pieces.common.PieceColor;
import com.fs.backend.exceptions.FinishedGameException;
import com.fs.backend.exceptions.IllegalMovementException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Match {
    private Long id;
    private LocalDateTime createdAt;
    private boolean isFinished;
    private boolean isWhiteTurn;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player winner;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private King whiteKing;
    private King blackKing;

    // TODO: THIS MUST BE ON THE SERVICE CLASS
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final HashMap<Character, Integer> COL_BY_ALGEBRAIC_SYMBOL = new HashMap<>(
            Map.ofEntries(
                    new AbstractMap.SimpleImmutableEntry<>('a', 1),
                    new AbstractMap.SimpleImmutableEntry<>('b', 2),
                    new AbstractMap.SimpleImmutableEntry<>('c', 3),
                    new AbstractMap.SimpleImmutableEntry<>('d', 4),
                    new AbstractMap.SimpleImmutableEntry<>('e', 5),
                    new AbstractMap.SimpleImmutableEntry<>('f', 6),
                    new AbstractMap.SimpleImmutableEntry<>('g', 7),
                    new AbstractMap.SimpleImmutableEntry<>('h', 8)
            ));
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final int BOARD_LENGHT = 8;

    public Match(String whitePlayerName, String blackPlayerName) {
        this.whitePlayer = new Player(whitePlayerName);
        this.blackPlayer = new Player(blackPlayerName);
        this.isWhiteTurn = true;
        isFinished = false;
        createdAt = LocalDateTime.now();
        createPieces();
    }

    private void createPieces() {
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        this.whiteKing = (King) PieceFactory.create('K', PieceColor.WHITE, new Pair(5, 1));
        this.blackKing = (King) PieceFactory.create('K', PieceColor.BLACK, new Pair(5, 8));

        whitePieces.add(PieceFactory.create('R', PieceColor.WHITE, new Pair(1, 1)));
        whitePieces.add(PieceFactory.create('N', PieceColor.WHITE, new Pair(2, 1)));
        whitePieces.add(PieceFactory.create('B', PieceColor.WHITE, new Pair(3, 1)));
        whitePieces.add(PieceFactory.create('Q', PieceColor.WHITE, new Pair(4, 1)));
        whitePieces.add(this.whiteKing);
        whitePieces.add(PieceFactory.create('B', PieceColor.WHITE, new Pair(6, 1)));
        whitePieces.add(PieceFactory.create('N', PieceColor.WHITE, new Pair(7, 1)));
        whitePieces.add(PieceFactory.create('R', PieceColor.WHITE, new Pair(8, 1)));

        blackPieces.add(PieceFactory.create('R', PieceColor.BLACK, new Pair(1, 8)));
        blackPieces.add(PieceFactory.create('N', PieceColor.BLACK, new Pair(2, 8)));
        blackPieces.add(PieceFactory.create('B', PieceColor.BLACK, new Pair(3, 8)));
        blackPieces.add(PieceFactory.create('Q', PieceColor.BLACK, new Pair(4, 8)));
        blackPieces.add(this.blackKing);
        blackPieces.add(PieceFactory.create('B', PieceColor.BLACK, new Pair(6, 8)));
        blackPieces.add(PieceFactory.create('N', PieceColor.BLACK, new Pair(7, 8)));
        blackPieces.add(PieceFactory.create('R', PieceColor.BLACK, new Pair(8, 8)));

        for (int col = 1; col <= 8; col++) {
            whitePieces.add(PieceFactory.create('P', PieceColor.WHITE, new Pair(col, 2)));
            blackPieces.add(PieceFactory.create('P', PieceColor.BLACK, new Pair(col, 7)));
        }
    }

    public void move(Piece pieceToMove, Pair target) throws IllegalMovementException {

        if (isFinished) {
            throw new FinishedGameException();
        }

        if (isWhiteTurn && pieceToMove.getColor() == PieceColor.BLACK) {
            throw new IllegalMovementException("You are trying to move a black piece but it's white's turn");
        }

        List<Piece> allies = pieceToMove.getColor() == PieceColor.BLACK ?
                blackPieces.stream().filter(p -> !p.equals(pieceToMove)).toList()
                : whitePieces.stream().filter(p -> !p.equals(pieceToMove)).toList();
        List<Piece> enemies = pieceToMove.getColor() == PieceColor.BLACK ? whitePieces : blackPieces;

        pieceToMove.move(target, BOARD_LENGHT, allies, enemies);
        this.isWhiteTurn = !this.isWhiteTurn;
        verifyCheckmate();
    }

    public void verifyCheckmate() {
        if (this.whiteKing.isCheckmate(BOARD_LENGHT,
                this.whitePieces.stream().filter(p -> !p.equals(this.whiteKing)).toList(),
                this.blackPieces
        )) {
            this.winner = this.blackPlayer;
            this.isFinished = true;
        }

        if (this.blackKing.isCheckmate(BOARD_LENGHT,
                this.blackPieces.stream().filter(p -> !p.equals(this.blackKing)).toList(),
                this.whitePieces)) {
            this.winner = this.whitePlayer;
            this.isFinished = true;
        }
    }
}

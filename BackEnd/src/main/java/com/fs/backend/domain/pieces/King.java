package com.fs.backend.domain.pieces;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.exceptions.GameInconsistencyException;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceBlockingException;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
public class King extends Piece {
    private boolean wasMoved;

    @Override
    public void checkMovement(Pair target) throws IllegalMovementException {

        if (!isLegalMovement(target)) {
            throw new IllegalMovementException("The move violates the King's movement rules");
        }
    }

    private boolean isLegalMovement(Pair target) {
        return Math.abs(target.getX() - this.position.getX()) <= 1
                && Math.abs(target.getY() - this.position.getY()) <= 1
                || target.getY() == this.position.getY() &&
                !this.wasMoved && Math.abs(target.getX() - this.position.getX()) == 2;
    }

    @Override
    public boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies) {
        return isLegalMovement(target) && !isMoveToSamePosition(target);
    }

    @Override
    protected void additionalStep(Pair target, List<Piece> allies, List<Piece> enemies)
            throws IllegalMovementException {

        boolean isUnderAttack = false;


        // Validate king under attack
        for (Piece enemy : enemies) {
            if (enemy.isAlive() && enemy.isValidAttack(target, allies, enemies)) {
                throw new IllegalMovementException("The move violates the King's movement rules");
            }
            if (enemy.isValidAttack(this.position, allies, enemies)) {
                isUnderAttack = true;
            }
        }

        // Validate castling
        List<Piece> rooks = allies.stream().filter(p -> p instanceof Rook).toList();

        if (rooks.size() == 2) {

            if (target.getY() == this.position.getY() && Math.abs(target.getX() - this.position.getX()) == 2) {

                boolean isShortCastling = target.getX() > this.position.getX();
                Rook alliedRook = (Rook) rooks.get(0);

                if (rooks.get(0).getPosition().getX() < rooks.get(1).getPosition().getX()
                        && isShortCastling) {
                    alliedRook = (Rook) rooks.get(1);
                }

                if (isUnderAttack || this.wasMoved || alliedRook.wasMoved()) {
                    throw new IllegalMovementException("The move violates the King's movement rules");
                } else {
                    if (isShortCastling) {
                        alliedRook.setPosition(new Pair(this.position.getX() + 1, this.position.getY()));
                    } else {
                        alliedRook.setPosition(new Pair(this.position.getX() - 1, this.position.getY()));
                    }

                    alliedRook.wasMoved(true);
                }
            }

            if (!wasMoved) {
                this.wasMoved = true;
            }
        } else {
            throw new GameInconsistencyException("Rooks missing from allied pieces");
        }
    }

    public boolean isCheckmate(int boardLegnth, List<Piece> allies, List<Piece> enemies) {

        List<Pair> availableSquares = getKingPosiblesMoves(boardLegnth, allies);
        int attackedSquaresAmount = 0;
        boolean isUnderAttack = false;

        for(Piece e : enemies) {
            try {
                if(!isUnderAttack && e.isValidAttack(this.position,
                        enemies.stream().filter(ae -> !ae.equals(e)).toList(), allies)) {
                    isUnderAttack = true;
                    break;
                }
            } catch (PieceBlockingException ignored) {
            }
        }

        for (Pair pm: availableSquares) {
            for (Piece e : enemies) {
                try {
                    if (e.isValidAttack(pm, enemies.stream().filter(ae -> !ae.equals(e)).toList(), allies)) {
                        attackedSquaresAmount++;
                    }
                } catch (PieceBlockingException ignored) {
                }
            }
        }

        return attackedSquaresAmount == availableSquares.size() && isUnderAttack;
    }

    private List<Pair> getKingPosiblesMoves(int boardLegnth, List<Piece> allies) {
        List<Pair> posiblesMoves = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;

                Pair possibleMove = new Pair(this.position.getX() + x, this.position.getY() + y);

                if (possibleMove.isValidWithinBounds(boardLegnth, 1)) {
                    if(allies.stream().noneMatch(a -> a.getPosition().getX() == possibleMove.getX()
                            && a.getPosition().getY() == possibleMove.getY())) {
                        posiblesMoves.add(possibleMove);
                    }
                }
            }
        }

        return posiblesMoves;
    }

    public boolean wasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }
}

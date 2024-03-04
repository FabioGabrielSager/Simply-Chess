package com.fs.matchapi.model.pieces;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import com.fs.matchapi.exceptions.GameInconsistencyException;
import com.fs.matchapi.exceptions.IllegalMovementException;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuperBuilder
public class King extends Piece {
    @Getter
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
    public boolean isReachableTarget(Pair target, List<Piece> allies, List<Piece> enemies) {
        return isLegalMovement(target) && !isMoveToSamePosition(target);
    }

    @Override
    protected void additionalStep(Pair target, List<Piece> allies, List<Piece> enemies)
            throws IllegalMovementException {

        boolean isUnderAttack = false;


        // Validate king under attack
        for (Piece enemy : enemies) {
            if (enemy.isAlive() && enemy.isReachableTarget(target, allies, enemies)) {
                throw new IllegalMovementException("The move violates the King's movement rules");
            }
            if (enemy.isReachableTarget(this.position, allies, enemies)) {
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

    public boolean isStaleMate(int boardLegnth, List<Piece> allies, List<Piece> enemies) {
        if (isUnderAttack(allies, enemies)) {
            return false;
        }

        List<Pair> availableSquares = getKingPossibleMoves(boardLegnth, allies);

        if (availableSquares.isEmpty()) {
            return false;
        }

        for (Pair as : availableSquares) {
            List<Piece> attackers = getAttackingPieces(as, allies, enemies);
            if (attackers.isEmpty()) {
                return false;
            }

            for (Piece a : attackers) {
                List<Pair> attackerPath = a.getPathToTarget(this.position);
                for (Pair step : attackerPath) {
                    if (this.canTargetBeProtected(a, step, allies, enemies)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean isCheckmate(int boardLegnth, List<Piece> allies, List<Piece> enemies) {
        List<Piece> attackingPieces = getAttackingPieces(this.position, allies, enemies);
        if (!attackingPieces.isEmpty()) {
            if (areAllPossibleMovesAttacked(boardLegnth, allies, enemies)) {
                if (attackingPieces.size() > 1) {
                    return true;
                } else {
                    return !canTargetBeProtected(attackingPieces.get(0), this.position, allies, enemies);
                }
            }
        }

        return false;
    }

    public boolean isUnderAttack(List<Piece> allies, List<Piece> enemies) {
        for (Piece e : enemies) {
            if (e.isAlive() && e.isReachableTarget(this.position,
                    enemies.stream().filter(ae -> !ae.equals(e)).toList(), allies)) {
                return true;
            }
        }

        return false;
    }

    private List<Piece> getAttackingPieces(Pair target, List<Piece> allies, List<Piece> enemies) {
        return enemies.stream().filter(p -> p.isReachableTarget(target, allies, enemies)).toList();
    }

    private boolean areAllPossibleMovesAttacked(int boardLegnth, List<Piece> allies, List<Piece> enemies) {
        List<Pair> availableSquares = getKingPossibleMoves(boardLegnth, allies);
        int attackedSquaresAmount = 0;

        for (Pair pm : availableSquares) {
            for (Piece e : enemies) {
                if (e.isReachableTarget(pm, enemies.stream().filter(ae -> !ae.equals(e)).toList(), allies)) {
                    attackedSquaresAmount++;
                    if (attackedSquaresAmount == availableSquares.size()) {
                        return true;
                    }
                }
            }
        }

        return availableSquares.isEmpty();
    }

    private boolean canTargetBeProtected(Piece attackingPiece, Pair squareToProtect, List<Piece> allies,
                                         List<Piece> enemies) {

        for (Pair step : attackingPiece.getPathToTarget(squareToProtect)) {
            for (Piece a : allies) {
                if(!(attackingPiece instanceof Knight)) {
                    if (a.isReachableTarget(step, allies, enemies)) {
                        return true;
                    }
                }
                if (a.isReachableTarget(attackingPiece.getPosition(), allies, enemies)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns all squares not occupied by allies that the king can move to,
     * regardless of whether the square is under attack or no.
     *
     * @param boardLength The length of the board.
     * @param allies      A list of allied pieces on the board.
     * @return A list of pairs representing possible moves for the king.
     */
    private List<Pair> getKingPossibleMoves(int boardLength, List<Piece> allies) {
        List<Pair> posiblesMoves = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0)
                    continue;

                Pair possibleMove = new Pair(this.position.getX() + x, this.position.getY() + y);

                if (possibleMove.isValidWithinBounds(boardLength, 1)) {
                    if (allies.stream().noneMatch(a -> a.isAlive() && a.getPosition().getX() == possibleMove.getX()
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

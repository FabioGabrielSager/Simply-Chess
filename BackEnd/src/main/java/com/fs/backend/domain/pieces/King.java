package com.fs.backend.domain.pieces;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.exceptions.GameInconsistencyException;
import com.fs.backend.exceptions.IllegalMovementException;
import lombok.experimental.SuperBuilder;

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



        for (Piece enemy : enemies) {
            if (enemy.isValidAttack(target, allies, enemies)) {
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

                if(rooks.get(0).getPosition().getX() < rooks.get(1).getPosition().getX()
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

    public boolean wasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }
}

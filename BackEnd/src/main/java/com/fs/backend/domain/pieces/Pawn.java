package com.fs.backend.domain.pieces;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.domain.pieces.common.PieceColor;
import com.fs.backend.exceptions.IllegalMovementException;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class Pawn extends Piece {

    private boolean wasMoved;

    @Override
    public void checkMovement(Pair target) throws IllegalMovementException {

        if (!isLegalMove(target)) {
            throw new IllegalMovementException("The move violates the Pawn's movement rules");
        }

    }

    private boolean isLegalMove(Pair target) {
        int direction = this.color.equals(PieceColor.WHITE) ? 1 : -1;

        if (target.getY() == this.position.getY() + direction) {
            if (this.position.getX() != target.getX()) {
                if (Math.abs(target.getX() - this.position.getX()) != 1) {
                    return false;
                }
            }
        } else if (target.getY() == this.position.getY() + 2 * direction && !wasMoved) {
            if (this.position.getX() != target.getX()) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    protected void additionalStep(Pair target, List<Piece> allies, List<Piece> enemies)
            throws IllegalMovementException {

        if (!isValidAttack(target, allies, enemies)) {
            throw new IllegalMovementException("The move violates the Pawn's movement rules");
        }

        if (!wasMoved)
            this.wasMoved = true;
    }

    @Override
    public boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies) {
        int direction = this.color.equals(PieceColor.WHITE) ? 1 : -1;

        if (target.getY() == this.position.getY() + direction
                && Math.abs(target.getX() - this.position.getX()) == 1) {
            if (enemies.stream().noneMatch(p -> p.getPosition().getX() == target.getX()
                    && p.getPosition().getY() == target.getY())) {
                return false;
            }
        }

        return !isMoveToSamePosition(target) && isLegalMove(target);
    }

    public boolean isPromoted(int boardLengh) {
        return this.color.equals(PieceColor.WHITE) && this.position.getY() == boardLengh
                || this.color.equals(PieceColor.BLACK) && this.position.getY() == 1;
    }

    public boolean wasMoved() {
        return wasMoved;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }
}

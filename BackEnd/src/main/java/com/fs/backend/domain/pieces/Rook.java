package com.fs.backend.domain.pieces;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.exceptions.PieceBlockingException;
import com.fs.backend.exceptions.IllegalMovementException;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class Rook extends Piece {

    private boolean wasMoved;

    @Override
    public void checkMovement(Pair target) throws IllegalMovementException {

        if (!isLegalMove(target)) {
            throw new IllegalMovementException("The move violates the Rook's movement rules");
        }

    }

    private boolean isLegalMove(Pair target) {
        return target.getX() == this.position.getX() || target.getY() == this.position.getY();
    }

    @Override
    protected boolean isTherePiecePathBlocking(Pair target, List<Piece> allies, List<Piece> enemies) {
        Pair actualPostion = this.position.clone();

        if (this.position.getX() != target.getX() && this.position.getY() == target.getY()) {
            for (int i = 1; i < Math.abs(target.getX() - this.position.getX()); i++) {
                actualPostion.setX(this.position.getX() + i * (target.getX() > this.position.getX() ? 1 : -1));

                if (allies.stream().anyMatch(p -> p.getPosition().getX() == actualPostion.getX())) {
                    return true;
                }

                if (enemies.stream().anyMatch(p -> p.getPosition().getX() == actualPostion.getX())) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i < Math.abs(target.getY() - this.position.getY()); i++) {
                actualPostion.setY(this.position.getY() + i * (target.getY() > this.position.getY() ? 1 : -1));

                if (allies.stream().anyMatch(p -> p.getPosition().getY() == actualPostion.getY())) {
                    return true;
                }

                if (enemies.stream().anyMatch(p -> p.getPosition().getY() == actualPostion.getY())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies) throws PieceBlockingException {
        return isLegalMove(target)
                && !isTherePiecePathBlocking(target, allies, enemies)
                && !isMoveToSamePosition(target);
    }

    @Override
    protected void additionalStep(Pair target, List<Piece> allies, List<Piece> enemies)
            throws IllegalMovementException {

        if (!wasMoved) {
            this.wasMoved = true;
        }

    }

    public boolean wasMoved() {
        return wasMoved;
    }

    public void wasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }
}

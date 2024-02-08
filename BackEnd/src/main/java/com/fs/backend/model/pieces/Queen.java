package com.fs.backend.model.pieces;

import com.fs.backend.model.pieces.common.Pair;
import com.fs.backend.model.pieces.common.Piece;
import com.fs.backend.exceptions.IllegalMovementException;
import com.fs.backend.exceptions.PieceBlockingException;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class Queen extends Piece {

    @Override
    public void checkMovement(Pair target) throws IllegalMovementException {
        if (!isLegalMove(target)) {
            throw new IllegalMovementException("The move violates the Queen's movement rules");
        }
    }

    private boolean isLegalMove(Pair target) {
        return this.position.getY() == target.getY() || this.position.getX() == target.getX() ||
                Math.abs(target.getX() - this.position.getX()) == Math.abs(target.getY() - this.position.getY());
    }

    @Override
    public boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies) throws PieceBlockingException {
        return isLegalMove(target)
                && !isTherePiecePathBlocking(target, allies, enemies)
                && !isMoveToSamePosition(target);
    }

    @Override
    protected boolean isTherePiecePathBlocking(Pair target, List<Piece> allies, List<Piece> enemies) {
        Pair actualPosition = this.position.clone();

        if (target.getY() == this.position.getY()) {
            for (int i = 1; i < Math.abs(target.getX() - this.position.getX()); i++) {
                if (allies.stream().anyMatch(
                        a -> a.isAlive() && a.getPosition().getX() == actualPosition.getX())) {
                    return true;
                }

                if (enemies.stream().anyMatch(
                        e -> e.isAlive() && e.getPosition().getX() == actualPosition.getX())) {
                    return true;
                }
            }
        } else if (target.getX() == this.position.getX()) {
            for (int i = 1; i < Math.abs(target.getY() - this.position.getY()); i++) {
                actualPosition.setY(this.position.getY() + i * (target.getY() > this.position.getY() ? 1 : -1));

                if (allies.stream().anyMatch(
                        a -> a.isAlive() && a.getPosition().getY() == actualPosition.getY())) {
                    return true;
                }

                if (enemies.stream().anyMatch(
                        e -> e.isAlive() && e.getPosition().getY() == actualPosition.getY())) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i < Math.abs(target.getX() - this.position.getX()); i++) {
                actualPosition.setX(this.position.getX() + i * (target.getX() > this.position.getX() ? 1 : -1));
                actualPosition.setY(this.position.getY() + i * (target.getY() > this.position.getY() ? 1 : -1));

                if (allies.stream().anyMatch(
                        a -> a.isAlive() && a.getPosition().getX() == actualPosition.getX()
                                && a.getPosition().getY() == actualPosition.getY())) {
                    return true;
                }

                if (enemies.stream().anyMatch(
                        e -> e.isAlive() && e.getPosition().getX() == actualPosition.getX()
                                && e.getPosition().getY() == actualPosition.getY())) {
                    return true;
                }
            }
        }

        return false;
    }
}

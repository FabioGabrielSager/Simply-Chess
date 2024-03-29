package com.fs.matchapi.model.pieces;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import com.fs.matchapi.exceptions.IllegalMovementException;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
public class Bishop extends Piece {

    @Override
    public void checkMovement(Pair target) throws IllegalMovementException {
        if (!isLegalMovement(target)) {
            throw new IllegalMovementException("The move violates the Bishop's movement rules");
        }
    }

    private boolean isLegalMovement(Pair target) {
        return Math.abs(target.getX() - this.position.getX()) == Math.abs(target.getY() - this.position.getY());
    }

    @Override
    public boolean isTherePiecePathBlocking(Pair target, List<Piece> allies, List<Piece> enemies) {
        Pair actualPosition = this.position.clone();

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
        
        return false;
    }

    @Override
    public boolean isReachableTarget(Pair target, List<Piece> allies, List<Piece> enemies) {
        return isLegalMovement(target)
                && !isTherePiecePathBlocking(target, allies, enemies)
                && !isMoveToSamePosition(target);
    }

    @Override
    public List<Pair> getPathToTarget(Pair target) {

        Pair actualPosition = this.position.clone();
        List<Pair> path = new ArrayList<>();

        for (int i = 1; i < Math.abs(target.getX() - this.position.getX()); i++) {
            actualPosition.setX(this.position.getX() + i * (target.getX() > this.position.getX() ? 1 : -1));
            actualPosition.setY(this.position.getY() + i * (target.getY() > this.position.getY() ? 1 : -1));

            path.add(actualPosition);
        }

        return path;
    }
}

package com.fs.backend.domain.pieces;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.exceptions.PieceBlockingException;
import com.fs.backend.exceptions.IllegalMovementException;
import lombok.experimental.SuperBuilder;

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

            if (allies.stream().anyMatch(a ->
                    a.getPosition().getX() == actualPosition.getX()
                            && a.getPosition().getY() == actualPosition.getY())) {
                return true;
            }

            if (enemies.stream().anyMatch(a ->
                    a.getPosition().getX() == actualPosition.getX()
                            && a.getPosition().getY() == actualPosition.getY())) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies) {
        return isLegalMovement(target)
                && !isTherePiecePathBlocking(target, allies, enemies)
                && !isMoveToSamePosition(target);
    }
}

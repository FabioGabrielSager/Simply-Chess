package com.fs.backend.domain.pieces;

import com.fs.backend.domain.pieces.common.Pair;
import com.fs.backend.domain.pieces.common.Piece;
import com.fs.backend.exceptions.IllegalMovementException;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class Knight extends Piece {

    @Override
    public void checkMovement(Pair target) throws IllegalMovementException {
        if(!isLegalMove(target)) {
            throw new IllegalMovementException("The move violates the Knight's movement rules");
        }
    }

    private boolean isLegalMove(Pair target) {
        return Math.abs(target.getX() - this.position.getX()) == 2
                && Math.abs(target.getY() - this.position.getY()) == 1
                || Math.abs(target.getX() - this.position.getX()) == 1
                && Math.abs(target.getY() - this.position.getY()) == 2;
    }

    @Override
    public boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies) {
        return isLegalMove(target) && !isMoveToSamePosition(target);
    }
}

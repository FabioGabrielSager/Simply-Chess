package com.fs.matchapi.model.pieces;

import com.fs.matchapi.model.pieces.common.Pair;
import com.fs.matchapi.model.pieces.common.Piece;
import com.fs.matchapi.exceptions.IllegalMovementException;
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
    public boolean isReachableTarget(Pair target, List<Piece> allies, List<Piece> enemies) {
        return isLegalMove(target) && !isMoveToSamePosition(target);
    }
}

package com.fs.matchapi.model.pieces.common;

import com.fs.matchapi.exceptions.PieceBlockingException;
import com.fs.matchapi.exceptions.DeadPieceMoveException;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.exceptions.OutOfBoundsException;
import com.fs.matchapi.exceptions.SamePositionException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@SuperBuilder
public abstract class Piece {

    protected Long id;
    protected Pair position;
    protected PieceColor color;
    protected boolean isAlive;

    public final void move(Pair target, int boardLength, List<Piece> allies, List<Piece> enemies)
            throws IllegalMovementException {

        if (!this.isAlive) {
            throw new DeadPieceMoveException();
        }

        if (isMoveToSamePosition(target)) {
            throw new SamePositionException();
        }

        if (!target.isValidWithinBounds(boardLength, 0)) {
            throw new OutOfBoundsException();
        }

        checkMovement(target);

        checkPathCollision(target, allies, enemies);

        additionalStep(target, allies, enemies);

        if (allies.stream().anyMatch(p -> p.position.getX() == target.getX() && p.position.getY() == target.getY())) {
            throw new PieceBlockingException("You are trying to move the piece towards an allied piece");
        }

        this.position = target;
        Optional<Piece> attakedPieceOptional = enemies.stream().filter(e -> e.isAlive()
                && e.getPosition().getY() == target.getY()
                && e.getPosition().getX() == target.getX()).findFirst();

        if (attakedPieceOptional.isPresent()) {
            attakedPieceOptional.get().setAlive(false);
        }
    }

    protected final boolean isMoveToSamePosition(Pair target) {
        return this.position.getX() == target.getX() && this.position.getY() == target.getY();
    }

    public abstract void checkMovement(Pair target) throws IllegalMovementException;

    protected final void checkPathCollision(Pair target, List<Piece> allies, List<Piece> enemies)
            throws PieceBlockingException {
        if (isTherePiecePathBlocking(target, allies, enemies)) {
            throw new PieceBlockingException("You are trying to move the piece through another piece");
        }
    }

    protected boolean isTherePiecePathBlocking(Pair target, List<Piece> allies, List<Piece> enemies) {
        return false;
    }

    protected void additionalStep(Pair target, List<Piece> allies, List<Piece> enemies) throws IllegalMovementException {

    }

    public abstract boolean isValidAttack(Pair target, List<Piece> allies, List<Piece> enemies)
            throws PieceBlockingException;
}

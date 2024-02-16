package com.fs.backend.exceptions;

public class DeadPieceMoveException extends IllegalMovementException {

    public DeadPieceMoveException() {
        super("You are trying to move a dead piece");
    }
}

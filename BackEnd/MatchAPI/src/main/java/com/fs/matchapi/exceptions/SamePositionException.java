package com.fs.matchapi.exceptions;

public class SamePositionException extends IllegalMovementException {

    public SamePositionException() {
        super("You are trying to move a piece to the same position");
    }
}

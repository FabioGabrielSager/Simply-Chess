package com.fs.matchapi.exceptions;

public class OutOfBoundsException extends IllegalMovementException {
    public OutOfBoundsException() {
        super("The move you are trying to make exceeds the limits of the board");
    }
}

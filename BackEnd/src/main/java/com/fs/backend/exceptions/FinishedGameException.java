package com.fs.backend.exceptions;

public class FinishedGameException extends IllegalStateException {
    public FinishedGameException() {
        super("Cannot make a move in a finished game");
    }
}

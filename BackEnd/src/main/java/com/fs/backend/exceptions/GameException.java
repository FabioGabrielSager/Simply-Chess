package com.fs.backend.exceptions;

public class GameException extends IllegalStateException {
    public GameException(String message) {
        super(message);
    }
}

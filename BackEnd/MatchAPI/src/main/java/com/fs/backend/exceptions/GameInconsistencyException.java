package com.fs.backend.exceptions;

public class GameInconsistencyException extends RuntimeException {
    public GameInconsistencyException(String message) {
        super(message);
    }
}

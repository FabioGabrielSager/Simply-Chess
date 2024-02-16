package com.fs.backend.exceptions;

public class PieceBlockingException extends IllegalMovementException {

    public PieceBlockingException(String message) {
        super(message);
    }

}

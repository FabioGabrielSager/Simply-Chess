package com.fs.matchapi.controller;

import com.fs.matchapi.common.ErrorApi;
import com.fs.matchapi.exceptions.GameException;
import com.fs.matchapi.exceptions.GameInconsistencyException;
import com.fs.matchapi.exceptions.IllegalMovementException;
import com.fs.matchapi.exceptions.PieceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@ControllerAdvice("com.fs.matchapi.controller")
public class ControllerExceptionHandler {
    @ExceptionHandler(IllegalMovementException.class)
    public ResponseEntity<ErrorApi> handleError(IllegalMovementException exception)
    {
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(GameException.class)
    public ResponseEntity<ErrorApi> handleError(GameException exception)
    {
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(GameInconsistencyException.class)
    public ResponseEntity<ErrorApi> handleError(GameInconsistencyException exception)
    {
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(PieceNotFoundException.class)
    public ResponseEntity<ErrorApi> handleError(PieceNotFoundException exception){
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApi> handleError(Exception exception){
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleError(MethodArgumentNotValidException exception){
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorApi> handleError(ResponseStatusException exception){
        ErrorApi error = buildError(exception.getReason(), HttpStatus.valueOf(exception.getStatusCode().value()));
        return ResponseEntity.status(exception.getStatusCode()).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorApi> handleError(EntityNotFoundException exception){
        ErrorApi error = buildError(exception.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @MessageExceptionHandler()
    public ErrorApi handleMessagingError(Exception exception) {
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(exception.getLocalizedMessage())
                .message(exception.getMessage())
                .build();
    }

    @MessageExceptionHandler()
    public ErrorApi handleMessagingError(MessagingException exception) {
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(exception.getLocalizedMessage())
                .message(exception.getMessage())
                .build();
    }

    private ErrorApi buildError(String message, HttpStatus httpStatus) {
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .build();
    }
}

package ro.msg.learning.bank.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.msg.learning.bank.dtos.ErrorDTO;
import ro.msg.learning.bank.exceptions.NotFoundException;

@RestControllerAdvice
public class ErrorHandlingAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorDTO handleNotFound(NotFoundException exception) {
        return ErrorDTO.of(exception);
    }
}

package com.mediko.mediko_server.global.exception;

import com.mediko.mediko_server.global.exception.exceptionType.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDTO> handleCustomException(CustomException e) {
        return ErrorDTO.toResponseEntity(e.getErrorCode());
    }
}

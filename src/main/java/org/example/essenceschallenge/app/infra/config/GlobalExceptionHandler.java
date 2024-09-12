package org.example.essenceschallenge.app.infra.config;

import org.example.essenceschallenge.app.infra.rest.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpException.class)
    @ResponseBody
    public ResponseEntity<String> handleHttpException(HttpException e) {
        HttpStatus status = HttpStatus.resolve(e.statusCode());
        if(status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(e.getMessage(), status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}

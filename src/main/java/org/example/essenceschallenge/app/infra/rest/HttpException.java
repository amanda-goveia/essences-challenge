package org.example.essenceschallenge.app.infra.rest;

public class HttpException extends RuntimeException {
    private final int statusCode;

    public HttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int statusCode() {
        return statusCode;
    }
}

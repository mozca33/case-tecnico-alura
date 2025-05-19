package br.com.alura.AluraFake.user.exceptions;

import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {
    private final HttpStatus status;

    public UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

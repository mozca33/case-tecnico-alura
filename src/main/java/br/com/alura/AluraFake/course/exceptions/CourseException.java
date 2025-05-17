package br.com.alura.AluraFake.course.exceptions;

import org.springframework.http.HttpStatus;

public class CourseException extends RuntimeException {
    private final HttpStatus status;

    public CourseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

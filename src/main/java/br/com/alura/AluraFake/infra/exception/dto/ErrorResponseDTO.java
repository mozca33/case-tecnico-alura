package br.com.alura.AluraFake.infra.exception.dto;

import org.springframework.http.HttpStatus;

public class ErrorResponseDTO {
    private final String message;
    private final String status;

    public ErrorResponseDTO(HttpStatus status, String message) {
        this.status = status.toString();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}

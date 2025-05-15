package br.com.alura.AluraFake.infra.exception.handler;

import br.com.alura.AluraFake.infra.exception.dto.ErrorResponseDTO;
import br.com.alura.AluraFake.task.exceptions.TaskException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessValidation(TaskException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponseDTO(ex.getStatus(), ex.getMessage()));
    }
}

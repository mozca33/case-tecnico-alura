package br.com.alura.AluraFake.infra.exception.handler;

import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.infra.exception.dto.ErrorResponseDTO;
import br.com.alura.AluraFake.task.exceptions.TaskException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<ErrorResponseDTO> handleTaskBusinessValidation(TaskException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponseDTO(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(CourseException.class)
    public ResponseEntity<ErrorResponseDTO> handleCourseBusinessValidation(CourseException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponseDTO(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(br.com.alura.AluraFake.user.exceptions.UserException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserBusinessValidation(
            br.com.alura.AluraFake.user.exceptions.UserException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponseDTO(ex.getStatus(), ex.getMessage()));
    }
}

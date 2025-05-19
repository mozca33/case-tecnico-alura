package br.com.alura.AluraFake.course.dto;

import org.hibernate.validator.constraints.Length;

import br.com.alura.AluraFake.course.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CourseDTO(
        Long id,
        @NotBlank @Length(min = 4, max = 80, message = "Title should have from 4 to 80 characters") String title,
        @NotBlank @Length(min = 4, max = 255, message = "Description should have from 4 to 255 characters") String description,
        @NotBlank @Email String emailInstructor,
        Status status) {
}

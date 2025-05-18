package br.com.alura.AluraFake.course.dto;

import org.hibernate.validator.constraints.Length;

import br.com.alura.AluraFake.course.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseDTO(
                Long id,
                @NotNull @NotBlank String title,
                @NotNull @NotBlank @Length(min = 4, max = 255) String description,
                @NotNull @NotBlank @Email String emailInstructor,
                @NotNull @NotBlank Status status) {
}

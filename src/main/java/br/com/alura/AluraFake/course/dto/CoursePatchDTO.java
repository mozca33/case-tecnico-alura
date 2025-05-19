package br.com.alura.AluraFake.course.dto;

import br.com.alura.AluraFake.course.enums.Status;

public record CoursePatchDTO(
        Long id,
        String title,
        String description,
        String emailInstructor,
        Status status) {
}

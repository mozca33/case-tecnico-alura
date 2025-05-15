package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.Type;

public record TaskPatchDTO(
        Long id,
        String statement,
        Type type,
        Integer order,
        Long courseId) {
    public TaskPatchDTO {
    }

}
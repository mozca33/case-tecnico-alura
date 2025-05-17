package br.com.alura.AluraFake.task.dto;

import java.util.List;

import br.com.alura.AluraFake.task.Type;

public record TaskPatchDTO(
        String statement,
        Type type,
        Integer order,
        Long courseId,
        List<TaskOptionDTO> options) {
    public TaskPatchDTO {
    }

    public TaskPatchDTO withType(Type newType) {
        return new TaskPatchDTO( this.statement, newType, this.order, this.courseId, this.options);
    }
}
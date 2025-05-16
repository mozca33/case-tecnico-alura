package br.com.alura.AluraFake.task.dto;

import java.util.List;

import br.com.alura.AluraFake.task.Type;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

public record TaskSingleChoiceDTO(
        Long id,
        @NotNull(message = "Course ID cannot be null or blank") Long courseId,
        @Size(min = 4, max = 255, message = "Task statement must be between 4 and 255 characters") @NotBlank(message = "Task statement cannot be null or blank") String statement,
        @NotNull(message = "Order cannot be null or blank") @Min(value = 1, message = "Order must be greater than or equal to 1") Integer order,
        Type type,
        @Size(min = 2, max = 5, message = "The task must have from 2 to 5 alternatives.") List<@Valid TaskOptionDTO> options) {
    public TaskSingleChoiceDTO {
    }

    public TaskSingleChoiceDTO withType(Type newType) {
        return new TaskSingleChoiceDTO(this.id, this.courseId, this.statement, this.order, newType, this.options);
    }

}
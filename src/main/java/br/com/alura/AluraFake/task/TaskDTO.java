package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskDTO(
        Long id,
        @NotNull(message = "Course ID cannot be null or blank") Long courseId,
        @Size(min = 4, max = 255, message = "Task statement must be between 4 and 255 characters") @NotBlank(message = "Task statement cannot be null or blank") String statement,
        @NotNull(message = "Order cannot be null or blank") @Min(value = 1, message = "Order must be greater than or equal to 1") @Max(value = 5, message = "It is not allowed to have more than 5 tasks per Course") Integer order,
        Type type) {
    public TaskDTO {
    }

    public TaskDTO withType(Type newType) {
        return new TaskDTO(this.id, this.courseId, this.statement, this.order, newType);
    }
}

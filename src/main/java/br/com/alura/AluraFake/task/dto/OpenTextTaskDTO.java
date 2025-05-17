package br.com.alura.AluraFake.task.dto;

import jakarta.validation.constraints.Min;

import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.task.models.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OpenTextTaskDTO(
        Long id,
        @NotNull(message = "Course ID cannot be null or blank") Long courseId,
        @Size(min = 4, max = 255, message = "Task statement must be between 4 and 255 characters") @NotBlank(message = "Task statement cannot be null or blank") String statement,
        @NotNull(message = "Order cannot be null or blank") @Min(value = 1, message = "Order must be greater than or equal to 1") Integer order,
        Type type) implements BaseTaskDTO {
    public OpenTextTaskDTO {
    }

    public OpenTextTaskDTO withType(Type newType) {
        return new OpenTextTaskDTO(this.id, this.courseId, this.statement, this.order, newType);
    }

    @Override
    public Task toEntity() {
        return new Task(id(), statement(), type(), order(), courseId());
    }

    @Override
    public Task toEntity(Long id) {
        return new Task(id, statement(), type(), order(), courseId());
    }

    @Override
    public Task toPartialEntity(Long id) {
        return new Task(id, statement(), type(), order(), courseId());

    }

    @Override
    public BaseTaskDTO toDTO() {
        return new OpenTextTaskDTO(id(), courseId(), statement(), order(), type());
    }
}

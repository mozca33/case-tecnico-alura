package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.models.Task;
import jakarta.validation.constraints.Min;
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

    @Override
    public OpenTextTaskDTO withType(Type newType) {
        return new OpenTextTaskDTO(this.id, this.courseId, this.statement, this.order, newType);
    }

    @Override
    public Task toEntity(Course course) {
        Task task = new Task(id(), statement(), type(), order(), course);

        return task;
    }

    @Override
    public Task toEntity(Long id, Course course) {
        Task task = new Task(id, statement(), type(), order(), course);

        return task;
    }

    @Override
    public Task toPartialEntity(Long id) {
        Task task = new Task(id, statement(), type(), order());

        return task;
    }

    @Override
    public Task toPartialEntity(Long id, Course course) {
        Task task = new Task(id, statement(), type(), order(), course);

        return task;
    }

    @Override
    public BaseTaskDTO toDTO() {
        return new OpenTextTaskDTO(id(), courseId(), statement(), order(), type());
    }
}

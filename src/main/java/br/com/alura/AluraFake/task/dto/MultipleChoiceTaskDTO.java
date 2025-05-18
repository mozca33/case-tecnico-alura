package br.com.alura.AluraFake.task.dto;

import java.util.List;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.task.mapper.TaskOptionMapper;
import br.com.alura.AluraFake.task.models.Task;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MultipleChoiceTaskDTO(
        Long id,
        @NotNull(message = "Course ID cannot be null or blank") Long courseId,
        @Size(min = 4, max = 255, message = "Task statement must be between 4 and 255 characters") @NotBlank(message = "Task statement cannot be null or blank") String statement,
        @NotNull(message = "Order cannot be null or blank") @Min(value = 1, message = "Order must be greater than or equal to 1") Integer order,
        Type type,
        @Size(min = 3, max = 5, message = "The task must have from 3 to 5 options.") List<@Valid TaskOptionDTO> options)
        implements BaseTaskDTO {
    public MultipleChoiceTaskDTO {
    }

    @Override
    public MultipleChoiceTaskDTO withType(Type newType) {
        return new MultipleChoiceTaskDTO(this.id, this.courseId, this.statement, this.order, newType, this.options);
    }

    @Override
    public Task toEntity(Course course) {
        Task task = new Task(id(), statement(), type(), order(), course);
        task.setOptions(TaskOptionMapper.toEntityList(options()));
        return task;
    }

    @Override
    public Task toEntity(Long id, Course course) {
        Task task = new Task(id, statement(), type(), order(), course);
        task.setOptions(TaskOptionMapper.toEntityList(options()));
        return task;
    }

    @Override
    public Task toPartialEntity(Long id) {
        Task task = new Task(id, statement(), type(), order());
        task.setOptions(TaskOptionMapper.toEntityList(options()));

        return task;
    }

    @Override
    public Task toPartialEntity(Long id, Course course) {
        Task task = new Task(id, statement(), type(), order(), course);
        task.setOptions(TaskOptionMapper.toEntityList(options()));

        return task;
    }

    @Override
    public BaseTaskDTO toDTO() {
        return new MultipleChoiceTaskDTO(id(), courseId(), statement(), order(), type(), options());
    }
}

package br.com.alura.AluraFake.task.dto;

import java.util.List;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.mapper.TaskOptionMapper;
import br.com.alura.AluraFake.task.models.Task;

public record TaskPatchDTO(
        String statement,
        Type type,
        Integer order,
        Long courseId,
        List<TaskOptionDTO> options) {
    public TaskPatchDTO {
    }

    public TaskPatchDTO withType(Type newType) {
        return new TaskPatchDTO(this.statement, newType, this.order, this.courseId, this.options);
    }

    public Task toPartialEntity(Long id) {
        Task task = new Task(id, statement(), type(), order());
        if (options() != null) {
            task.setOptions(TaskOptionMapper.toEntityList(options()));
        }

        return task;
    }

    public Task toPartialEntity(Long id, Course course) {
        Task task = new Task(id, statement(), type(), order(), course);
        if (options() != null) {
            task.setOptions(TaskOptionMapper.toEntityList(options()));
        }

        return task;
    }
}
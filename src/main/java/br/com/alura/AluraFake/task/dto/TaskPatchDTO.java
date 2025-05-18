package br.com.alura.AluraFake.task.dto;

import java.util.List;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.models.TaskOption;

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
            List<TaskOption> listOptions = options().stream()
                    .map(opt -> new TaskOption(opt.option(), opt.isCorrect())).toList();
            task.setOptions(listOptions);
        }
        return task;
    }

    public Task toPartialEntity(Long id, Course course) {
        Task task = new Task(id, statement(), type(), order(), course);
        if (options() != null) {
            List<TaskOption> listOptions = options().stream()
                    .map(opt -> new TaskOption(opt.option(), opt.isCorrect())).toList();
            task.setOptions(listOptions);
        }
        return task;
    }
}
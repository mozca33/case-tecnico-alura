package br.com.alura.AluraFake.task.mapper;

import br.com.alura.AluraFake.task.dto.TaskDTO;
import br.com.alura.AluraFake.task.models.Task;

public class TaskMapper {
    public static Task toEntity(TaskDTO taskDTO) {
        Task task = new Task(taskDTO.statement(), taskDTO.type(), taskDTO.order(), taskDTO.courseId());
        return task;
    }

    public static Task toEntity(Long taskId, TaskDTO taskDTO) {
        Task task = new Task(taskId, taskDTO.statement(), taskDTO.type(), taskDTO.order(), taskDTO.courseId());
        return task;
    }

    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(task.getId(), task.getCourseId(), task.getStatement(), task.getOrder(),
                task.getType());
    }
}

package br.com.alura.AluraFake.task.mapper;

import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.models.Task;

public class TaskPatchMapper {

    public static Task toPartialEntity(TaskPatchDTO dto) {
        Task task = new Task();
        task.setStatement(dto.statement());
        task.setType(dto.type());
        task.setOrder(dto.order());
        task.setCourseId(dto.courseId());

        return task;
    }
}

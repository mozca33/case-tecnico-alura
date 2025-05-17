package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import br.com.alura.AluraFake.task.dto.BaseTaskDTO;
import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.models.Task;

public class TaskMapper {
    public static Task toEntity(BaseTaskDTO dto) {
        return dto.toEntity();
    }

    public static Task toEntity(Long id, BaseTaskDTO dto) {
        return dto.toEntity(id);
    }

    public static Task toPartialEntity(Long id, TaskPatchDTO dto) {
        return dto.toPartialEntity(id);
    }

    public static BaseTaskDTO toDTO(Task task) {
        return task.toDTO();
    }

    public static List<BaseTaskDTO> toDTO(List<Task> tasks) {
        return tasks.stream().map(Task::toDTO).toList();
    }

}

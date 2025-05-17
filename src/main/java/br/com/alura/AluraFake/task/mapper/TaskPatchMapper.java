package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.models.TaskOption;

public class TaskPatchMapper {

    public static Task toPartialEntity(Long id, TaskPatchDTO dto) {
        Task task = new Task(id, dto.statement(), dto.type(), dto.order(), dto.courseId());
        if (dto.options() != null) {
            List<TaskOption> options = dto.options().stream()
                    .map(opt -> new TaskOption(opt.option(), opt.isCorrect()))
                    .toList();

            task.setOptions(options);
        }

        return task;
    }
}

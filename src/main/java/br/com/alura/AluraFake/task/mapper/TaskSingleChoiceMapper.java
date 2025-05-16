package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import br.com.alura.AluraFake.task.dto.TaskSingleChoiceDTO;
import br.com.alura.AluraFake.task.models.TaskOption;
import br.com.alura.AluraFake.task.models.Task;

public class TaskSingleChoiceMapper {
    public static Task toEntity(TaskSingleChoiceDTO taskDTO) {
        Task task = new Task(taskDTO.statement(), taskDTO.type(), taskDTO.order(), taskDTO.courseId());
        if (taskDTO.options() != null) {
            List<TaskOption> options = taskDTO.options().stream()
                    .map(opt -> new TaskOption(opt.option(), opt.isCorrect()))
                    .toList();

            task.setOptions(options);

        }

        return task;
    }

    public static TaskSingleChoiceDTO toDTO(Task task) {
        var optionDTO = task.getOptions().stream()
                .map(opt -> new TaskOptionDTO(opt.getTaskOption(), opt.getCorrect()))
                .toList();
        return new TaskSingleChoiceDTO(task.getId(), task.getCourseId(), task.getStatement(), task.getOrder(),
                task.getType(), optionDTO);
    }
}

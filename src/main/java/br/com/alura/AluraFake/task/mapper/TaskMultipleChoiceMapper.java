package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import br.com.alura.AluraFake.task.dto.TaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.models.TaskOption;

public class TaskMultipleChoiceMapper {
    public static Task toEntity(TaskMultipleChoiceDTO taskDTO) {
        Task task = new Task(taskDTO.statement(), taskDTO.type(), taskDTO.order(), taskDTO.courseId());
        if (taskDTO.options() != null) {
            List<TaskOption> options = taskDTO.options().stream()
                    .map(opt -> new TaskOption(opt.option(), opt.isCorrect()))
                    .toList();

            task.setOptions(options);

        }

        return task;
    }

    public static Task toEntity(Long taskId, TaskMultipleChoiceDTO taskDTO) {
        Task task = new Task(taskId, taskDTO.statement(), taskDTO.type(), taskDTO.order(), taskDTO.courseId());
        if (taskDTO.options() != null) {
            List<TaskOption> options = taskDTO.options().stream()
                    .map(opt -> new TaskOption(opt.option(), opt.isCorrect()))
                    .toList();

            task.setOptions(options);

        }

        return task;
    }

    public static TaskMultipleChoiceDTO toDTO(Task task) {
        var optionDTO = task.getOptions().stream()
                .map(opt -> new TaskOptionDTO(opt.getTaskOption(), opt.getCorrect()))
                .toList();
        return new TaskMultipleChoiceDTO(task.getId(), task.getCourseId(), task.getStatement(), task.getOrder(),
                task.getType(), optionDTO);
    }
}

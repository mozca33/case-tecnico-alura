package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import org.springframework.http.HttpStatus;

import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.models.TaskOption;

public class TaskOptionMapper {
    public static List<TaskOption> toEntityList(List<TaskOptionDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        for (TaskOptionDTO option : dtos) {
            if (option.option() == null || option.isCorrect() == null) {
                throw new TaskException("Option fields must not be null or blank", HttpStatus.BAD_REQUEST);
            }
        }

        return dtos.stream()
                .map(dto -> new TaskOption(dto.option(), dto.isCorrect()))
                .toList();
    }
}

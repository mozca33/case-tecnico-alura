package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import br.com.alura.AluraFake.task.models.TaskOption;

public class TaskOptionMapper {
    public static List<TaskOption> toEntityList(List<TaskOptionDTO> dtos) {
        return dtos == null ? List.of() : dtos.stream().map(TaskOptionDTO::toEntity).toList();
    }
}

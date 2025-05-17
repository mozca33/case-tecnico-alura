package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.task.models.Task;

public interface BaseTaskDTO {
    Long id();

    Long courseId();

    String statement();

    Integer order();

    Type type();

    Task toEntity();

    Task toEntity(Long id);

    Task toPartialEntity(Long id);

    BaseTaskDTO toDTO();

    BaseTaskDTO withType(Type type);
}
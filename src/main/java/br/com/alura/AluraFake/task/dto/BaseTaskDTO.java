package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.models.Task;

public interface BaseTaskDTO {
    Long id();

    Long courseId();

    String statement();

    Integer order();

    Type type();

    Task toEntity(Course course);

    Task toEntity(Long id, Course course);

    Task toPartialEntity(Long id);

    Task toPartialEntity(Long id, Course course);

    BaseTaskDTO toDTO();

    BaseTaskDTO withType(Type type);
}
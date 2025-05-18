package br.com.alura.AluraFake.task.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.task.dto.BaseTaskDTO;
import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.models.Task;

@Component
public class TaskMapper {
    private final CourseService courseService;

    public TaskMapper(CourseService courseService) {
        this.courseService = courseService;
    }

    public Task toEntity(BaseTaskDTO dto) {
        if (dto == null) {
            return null;
        }
        Course course = courseService.getById(dto.courseId());

        return dto.toEntity(course);
    }

    public Task toEntity(Long id, BaseTaskDTO dto) {
        if (dto == null) {
            return null;
        }
        Course course = courseService.getById(dto.courseId());

        return dto.toEntity(id, course);
    }

    public Task toPartialEntity(Long id, TaskPatchDTO dto) {
        if (dto.courseId() != null) {
            Course course = courseService.getById(dto.courseId());
            return dto.toPartialEntity(id, course);
        }

        return dto.toPartialEntity(id);
    }

    public BaseTaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }
        return task.toDTO();
    }

    public List<BaseTaskDTO> toDTO(List<Task> tasks) {
        return tasks.stream().map(Task::toDTO).toList();
    }

}

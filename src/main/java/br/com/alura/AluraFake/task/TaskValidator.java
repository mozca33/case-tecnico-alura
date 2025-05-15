package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.exceptions.TaskException;

@Component
public class TaskValidator {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    public TaskValidator(CourseRepository courseRepository, TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    public void validateForCreate(Task newTask) {
        validateCourseIsInBuildingStatus(newTask.getCourseId());
        validateUniqueStatementForCreate(newTask);
    }

    public void validateForUpdate(Task newTask) {
        validateCourseIsInBuildingStatus(newTask.getCourseId());
        validateUniqueStatementForUpdate(newTask);
    }

    public void validateOrderSequence(Long courseId, Integer order) {
        if (order == 1)
            return;

        if (!taskRepository.existsByCourseIdAndOrder(courseId, order - 1)) {
            throw new TaskException("Task order is not sequential.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCourseIsInBuildingStatus(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new TaskException("Course " + courseId + " not found.", HttpStatus.NOT_FOUND));

        if (course.getStatus() != Status.BUILDING) {
            throw new TaskException("Course " + courseId + " is not in BUILDING status.",
                    HttpStatus.CONFLICT);
        }
    }

    private void validateUniqueStatementForCreate(Task task) {
        List<Task> existingTasks = taskRepository.findByCourseId(task.getCourseId());
        boolean statementExists = existingTasks.stream()
                .anyMatch(t -> t.getStatement().equals(task.getStatement()));

        if (statementExists) {
            throw new TaskException("Task statement already exists.", HttpStatus.CONFLICT);
        }
    }

    private void validateUniqueStatementForUpdate(Task task) {
        List<Task> existingTasks = taskRepository.findByCourseId(task.getCourseId());
        boolean statementExists = existingTasks.stream()
                .anyMatch(t -> !t.getId().equals(task.getId()) &&
                        t.getStatement().equals(task.getStatement()));

        if (statementExists) {
            throw new TaskException("Task statement already exists.", HttpStatus.CONFLICT);
        }
    }
}

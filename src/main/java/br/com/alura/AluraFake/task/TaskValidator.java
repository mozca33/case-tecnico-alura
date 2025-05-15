package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;

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
            throw new RuntimeException("Task order is not sequential.");
        }
    }

    private void validateCourseIsInBuildingStatus(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course " + courseId + " not found."));

        if (course.getStatus() != Status.BUILDING) {
            throw new RuntimeException("Course " + courseId + " is not in BUILDING status.");
        }
    }

    private void validateUniqueStatementForCreate(Task task) {
        List<Task> existingTasks = taskRepository.findByCourseId(task.getCourseId());
        boolean statementExists = existingTasks.stream()
                .anyMatch(t -> t.getStatement().equals(task.getStatement()));

        if (statementExists) {
            throw new RuntimeException("Task statement already exists.");
        }
    }

    private void validateUniqueStatementForUpdate(Task task) {
        List<Task> existingTasks = taskRepository.findByCourseId(task.getCourseId());
        boolean statementExists = existingTasks.stream()
                .anyMatch(t -> !t.getId().equals(task.getId()) &&
                        t.getStatement().equals(task.getStatement()));

        if (statementExists) {
            throw new RuntimeException("Task statement already exists.");
        }
    }
}

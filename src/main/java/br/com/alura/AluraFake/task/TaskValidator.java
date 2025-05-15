package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.task.exceptions.TaskException;

@Component
public class TaskValidator {

    private final TaskRepository taskRepository;

    public TaskValidator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void validateForCreate(Task newTask) {
        validateOrderSequence(newTask.getCourseId(), newTask.getOrder());
        validateUniqueStatementForCreate(newTask);
    }

    public void validateForUpdate(Task newTask) {
        validateOrderSequence(newTask.getCourseId(), newTask.getOrder());
        validateUniqueStatementForUpdate(newTask);
    }

    private void validateOrderSequence(Long courseId, Integer order) {
        if (order == 1)
            return;

        if (!taskRepository.existsByCourseIdAndOrder(courseId, order - 1)) {
            throw new TaskException("Task order is not sequential.", HttpStatus.BAD_REQUEST);
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

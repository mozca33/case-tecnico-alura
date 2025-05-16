package br.com.alura.AluraFake.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.models.TaskOption;

@Component
public class TaskValidator {

    private final TaskRepository taskRepository;

    public TaskValidator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void validateForCreate(Task newTask) {
        validateTaskLimit(newTask.getCourseId());
        validateOrderSequence(newTask.getCourseId(), newTask.getOrder());
        validateUniqueStatementForCreate(newTask);
        if (newTask.isSingleChoice()) {
            validateSingleChoiceOptions(newTask);
        }
    }

    public void validateForUpdate(Task newTask) {
        validateOrderSequence(newTask.getCourseId(), newTask.getOrder());
        validateUniqueStatementForUpdate(newTask);
    }

    private void validateTaskLimit(Long courseId) {
        long taskCount = taskRepository.countByCourseId(courseId);
        if (taskCount >= 5) {
            throw new TaskException("Task limit reached for course " + courseId + ", maximum is 5.",
                    HttpStatus.BAD_REQUEST);
        }
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

    private void validateSingleChoiceOptions(Task task) {
        List<TaskOption> options = task.getOptions();
        Set<String> seenTexts = new HashSet<>();

        if (options == null || options.isEmpty()) {
            throw new TaskException("Single choice task must have between 2 and 5 options.",
                    HttpStatus.BAD_REQUEST);
        }

        if (options.stream().filter(TaskOption::getCorrect).count() != 1) {
            throw new TaskException("Single choice task must have exactly one correct option.",
                    HttpStatus.BAD_REQUEST);
        }

        for (TaskOption option : options) {

            if (task.getStatement().toLowerCase().equals(option.getTaskOption().toLowerCase())) {
                throw new TaskException("Option text cannot be the same as the task statement.",
                        HttpStatus.BAD_REQUEST);
            }

            if (!seenTexts.add(option.getTaskOption().toLowerCase())) {
                throw new TaskException("Duplicate option text found: " + option.getTaskOption(),
                        HttpStatus.BAD_REQUEST);
            }
        }

    }
}

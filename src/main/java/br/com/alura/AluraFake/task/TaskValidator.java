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
    private static final int MAX_TASKS_PER_COURSE = 5;
    private static final int MIN_SINGLE_CHOICE_OPTIONS = 2;
    private static final int MIN_MULTIPLE_CHOICE_OPTIONS = 3;
    private static final int MAX_OPTIONS = 5;
    private static final int MAX_SINGLE_CHOICE_CORRECT_OPTIONS = 1;
    private static final int MIN_MULTIPLE_CHOICES_CORRECT_OPTIONS = 2;
    private static final int MAX_MULTIPLE_CHOICES_CORRECT_OPTIONS = 4;

    public TaskValidator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void validateForCreate(Task newTask) {
        validateTaskLimit(newTask.getCourse().getId());
        validateOrderSequence(newTask, newTask.getOrder());
        validateUniqueStatementForCreate(newTask);
        if (newTask.isSingleChoice()) {
            validateOptions(newTask,
                    MIN_SINGLE_CHOICE_OPTIONS,
                    MAX_OPTIONS,
                    MAX_SINGLE_CHOICE_CORRECT_OPTIONS,
                    MAX_SINGLE_CHOICE_CORRECT_OPTIONS);
        }
        if (newTask.isMultipleChoice()) {
            validateOptions(newTask,
                    MIN_MULTIPLE_CHOICE_OPTIONS,
                    MAX_OPTIONS,
                    MIN_MULTIPLE_CHOICES_CORRECT_OPTIONS,
                    MAX_MULTIPLE_CHOICES_CORRECT_OPTIONS);
        }
    }

    public void validateForUpdate(Task newTask) {
        validateOrderSequence(newTask, newTask.getOrder());
        validateUniqueStatementForUpdate(newTask);
        if (newTask.isSingleChoice()) {
            validateOptions(newTask,
                    MIN_SINGLE_CHOICE_OPTIONS,
                    MAX_OPTIONS,
                    MAX_SINGLE_CHOICE_CORRECT_OPTIONS,
                    MAX_SINGLE_CHOICE_CORRECT_OPTIONS);
        }
        if (newTask.isMultipleChoice()) {
            validateOptions(newTask,
                    MIN_MULTIPLE_CHOICE_OPTIONS,
                    MAX_OPTIONS,
                    MIN_MULTIPLE_CHOICES_CORRECT_OPTIONS,
                    MAX_MULTIPLE_CHOICES_CORRECT_OPTIONS);
        }
    }

    private void validateTaskLimit(Long courseId) {
        int taskCount = taskRepository.countByCourseId(courseId);
        if (taskCount >= MAX_TASKS_PER_COURSE) {
            throw new TaskException("Task limit reached for course " + courseId + ", maximum is 5.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateOrderSequence(Task task, Integer order) {
        Task previousTask, nextTask;
        if (order == 1)
            return;

        if (task.getId() != null) {
            previousTask = taskRepository.findTopByCourseIdAndOrderAndIdNot(task.getCourse().getId(), order - 1,
                    task.getId());
            nextTask = taskRepository.findTopByCourseIdAndOrderAndIdNot(task.getCourse().getId(), order, task.getId());
        } else {
            previousTask = taskRepository.findTopByCourseIdAndOrder(task.getCourse().getId(), order - 1);
            nextTask = taskRepository.findTopByCourseIdAndOrder(task.getCourse().getId(), order);
        }

        if (previousTask == null) {
            throw new TaskException("Task order is not sequential.", HttpStatus.BAD_REQUEST);
        }
        if (previousTask.getId().equals(task.getId()) && nextTask == null) {
            throw new TaskException("Task order is not sequential.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateUniqueStatementForCreate(Task task) {
        boolean statementExists = taskRepository.existsByCourseIdAndStatement(task.getCourse().getId(),
                task.getStatement());

        if (statementExists) {
            throw new TaskException("Task statement already exists.", HttpStatus.CONFLICT);
        }
    }

    private void validateUniqueStatementForUpdate(Task task) {

        boolean statementExists = taskRepository.existsByCourseIdAndStatementAndIdNot(task.getCourse().getId(),
                task.getStatement(), task.getId());

        if (statementExists) {
            throw new TaskException("Task statement already exists.", HttpStatus.CONFLICT);
        }
    }

    private void validateOptions(Task task, int minOptions, int maxOptions, int minCorrect, int maxCorrect) {
        List<TaskOption> options = task.getOptions();
        Set<String> seenTexts = new HashSet<>();

        if (options == null || options.isEmpty() || options.size() < minOptions || options.size() > maxOptions) {
            throw new TaskException("Task must have between " + minOptions + " and " + maxOptions + " options.",
                    HttpStatus.BAD_REQUEST);
        }

        long correctCount = options.stream().filter(TaskOption::getCorrect).count();
        if (correctCount < minCorrect || correctCount > maxCorrect) {
            if (minCorrect == MAX_SINGLE_CHOICE_CORRECT_OPTIONS && minCorrect == maxCorrect) {
                throw new TaskException(
                        "Task must have exactly " + MAX_SINGLE_CHOICE_CORRECT_OPTIONS + " correct option.",
                        HttpStatus.BAD_REQUEST);
            }
            throw new TaskException(
                    "Task must have between " + MIN_MULTIPLE_CHOICES_CORRECT_OPTIONS + " and "
                            + MAX_MULTIPLE_CHOICES_CORRECT_OPTIONS + " correct options.",
                    HttpStatus.BAD_REQUEST);
        }

        for (TaskOption option : options) {

            if (task.getStatement().equalsIgnoreCase(option.getTaskOption())) {
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

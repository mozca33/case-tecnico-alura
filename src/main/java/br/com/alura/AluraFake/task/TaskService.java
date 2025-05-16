package br.com.alura.AluraFake.task;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.course.CourseValidator;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.models.Task;
import jakarta.transaction.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskValidator taskValidator;
    private final CourseValidator courseValidator;

    public TaskService(TaskRepository taskRepository, TaskValidator taskValidator, CourseValidator courseValidator) {
        this.courseValidator = courseValidator;
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
    }

    @Transactional
    public Task createTask(Task task) {
        switch (task.getType()) {
            case OPEN_TEXT:
                return createOpenTextTask(task);
            case SINGLE_CHOICE:
                return createSingleChoiceTask(task);
            default:
                throw new TaskException("Unknown task type " + task.getType() + ".", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    private Task createSingleChoiceTask(Task task) {
        courseValidator.validateCourseIsInBuildingStatus(task.getCourseId());
        taskValidator.validateForCreate(task);
        taskRepository.updateTaskOrderForInsert(task.getCourseId(), task.getOrder());
        attachOptionsToTask(task);

        return taskRepository.save(task);
    }

    @Transactional
    private Task createOpenTextTask(Task task) {
        courseValidator.validateCourseIsInBuildingStatus(task.getCourseId());
        taskValidator.validateForCreate(task);
        taskRepository.updateTaskOrderForInsert(task.getCourseId(), task.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long taskId, Task task) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException("Task " + taskId + " not found.", HttpStatus.NOT_FOUND));

        if (existingTask.isSameAs(task)) {
            return existingTask;
        }

        taskValidator.validateForUpdate(task);
        if (!existingTask.getOrder().equals(task.getOrder())) {
            adjustTaskOrder(existingTask, task.getOrder());
        }

        updateExistingTask(existingTask, task);

        return taskRepository.save(existingTask);
    }

    @Transactional
    public Task patchTask(Long taskId, Task task) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException("Task " + taskId + " not found.", HttpStatus.NOT_FOUND));

        mergeTaskUpdates(existingTask, task);

        if (existingTask.isSameAs(task) || Task.isEmpty(task)) {
            return existingTask;
        }

        taskValidator.validateForUpdate(existingTask);

        return taskRepository.save(existingTask);
    }

    private void adjustTaskOrder(Task existingTask, Integer newOrder) {
        if (newOrder == null)
            return;

        if (newOrder > existingTask.getOrder()) {
            taskRepository.decrementOrderRange(existingTask.getCourseId(), existingTask.getOrder() + 1, newOrder);
        } else {
            taskRepository.incrementOrderRange(existingTask.getCourseId(), newOrder, existingTask.getOrder() - 1);
        }
    }

    private void updateExistingTask(Task existingTask, Task task) {
        existingTask.setStatement(task.getStatement());
        existingTask.setOrder(task.getOrder());
        existingTask.setCourseId(task.getCourseId());
    }

    private void mergeTaskUpdates(Task existingTask, Task task) {
        if (task.getStatement() != null) {
            existingTask.setStatement(task.getStatement());
        }

        if (task.getOrder() != null) {
            adjustTaskOrder(existingTask, task.getOrder());
            existingTask.setOrder(task.getOrder());
        }

        if (task.getType() != null)
            existingTask.setType(task.getType());

        if (task.getCourseId() != null)
            existingTask.setCourseId(task.getCourseId());
    }

    private void attachOptionsToTask(Task task) {
        if (task.getOptions() != null) {
            task.getOptions().forEach(option -> option.setTask(task));
        }
    }
}

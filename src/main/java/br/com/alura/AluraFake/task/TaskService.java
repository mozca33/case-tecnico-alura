package br.com.alura.AluraFake.task;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.task.exceptions.TaskException;
import jakarta.transaction.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskValidator taskValidator;

    public TaskService(TaskRepository taskRepository, TaskValidator taskValidator) {
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
    }

    @Transactional
    public Task createTask(Task task) {
        switch (task.getType()) {
            case OPEN_TEXT:
                return createOpenTextTask(task);
            default:
                throw new TaskException("Unknown task type " + task.getType() + ".", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public Task createOpenTextTask(Task task) {
        taskValidator.validateForCreate(task);
        taskValidator.validateOrderSequence(task.getCourseId(), task.getOrder());
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

    private void adjustTaskOrder(Task existingTask, Integer newOrder) {
        if (newOrder > existingTask.getOrder()) {
            taskRepository.decrementOrderRange(existingTask.getCourseId(), existingTask.getOrder() + 1, newOrder);
        } else {
            taskRepository.incrementOrderRange(existingTask.getCourseId(), newOrder, existingTask.getOrder() - 1);
        }
    }

    private void updateExistingTask(Task existingTask, Task task) {
        existingTask.setStatement(task.getStatement());
        existingTask.setOrder(task.getOrder());
        existingTask.setType(task.getType());
        existingTask.setCourseId(task.getCourseId());
    }
}

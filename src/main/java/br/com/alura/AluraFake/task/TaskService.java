package br.com.alura.AluraFake.task;

import java.util.List;

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
    private final TaskOrderService taskOrderService;

    public TaskService(TaskRepository taskRepository, TaskValidator taskValidator, CourseValidator courseValidator,
            TaskOrderService taskOrderService) {
        this.courseValidator = courseValidator;
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
        this.taskOrderService = taskOrderService;
    }

    public List<Task> findTasksByCourseId(Long id) {
        courseValidator.validateCourseExistsById(id);
        List<Task> list = taskRepository.findByCourseId(id);

        return list;
    }

    @Transactional
    public Task createTask(Task task) {
        return switch (task.getType()) {
            case OPEN_TEXT -> createAndPersistTask(task, false);
            case SINGLE_CHOICE, MULTIPLE_CHOICE -> createAndPersistTask(task, true);
            default -> throw new TaskException("Unknown task type " + task.getType() + ".", HttpStatus.BAD_REQUEST);
        };
    }

    @Transactional
    public Task updateTask(Task task) {
        Integer newOrder;
        Task existingTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new TaskException("Task " + task.getId() + " not found.", HttpStatus.NOT_FOUND));
        task.ensureSameTypeAs(existingTask);

        if (existingTask.isSameAs(task) || task.isEmpty()) {
            return existingTask;
        }

        newOrder = existingTask.mergeFrom(task);
        if (newOrder != null && !existingTask.getOrder().equals(task.getOrder())) {
            taskOrderService.adjustOrderForUpdate(existingTask, task.getOrder());
            existingTask.setOrder(newOrder);
        }
        courseValidator.validateCourseIsInBuildingStatus(existingTask.getCourseId());
        taskValidator.validateForUpdate(existingTask);

        return taskRepository.save(existingTask);
    }

    private Task createAndPersistTask(Task task, boolean hasOptions) {
        prepareTaskForCreation(task);
        if (hasOptions) {
            task.attachOptionsToTask();
        }

        return taskRepository.save(task);
    }

    private void prepareTaskForCreation(Task task) {
        courseValidator.validateCourseIsInBuildingStatus(task.getCourseId());
        taskValidator.validateForCreate(task);
        taskRepository.updateTaskOrderForInsert(task.getCourseId(), task.getOrder());
    }

    public void deleteById(Long id) {
        if (id <= 0) {
            throw new TaskException("Id must be a positive value.", HttpStatus.BAD_REQUEST);
        }

        if (!taskRepository.existsById(id)) {
            throw new TaskException("Task with id " + id + " does not exist.", HttpStatus.NOT_FOUND);
        }

        taskRepository.deleteById(id);
    }
}

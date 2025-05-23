package br.com.alura.AluraFake.task.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.validator.TaskValidator;
import br.com.alura.AluraFake.course.service.CourseService;
import jakarta.transaction.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskValidator taskValidator;
    private final CourseService courseService;
    private final TaskOrderService taskOrderService;

    public TaskService(TaskRepository taskRepository, TaskValidator taskValidator, CourseService courseService,
            TaskOrderService taskOrderService) {
        this.courseService = courseService;
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
        this.taskOrderService = taskOrderService;
    }

    public List<Task> findTasksByCourseId(Long id) {
        courseService.getById(id);
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
        taskValidator.validatePositiveId(task.getId());
        Task existingTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new TaskException("Task " + task.getId() + " not found.", HttpStatus.NOT_FOUND));
        existingTask.ensureSameTypeAs(task);

        if (existingTask.isSameAs(task) || task.isEmpty()) {
            return existingTask;
        }

        newOrder = existingTask.mergeFrom(task);
        if (newOrder != null && !existingTask.getOrder().equals(task.getOrder())) {
            taskOrderService.adjustOrderForUpdate(existingTask, task.getOrder());
            existingTask.setOrder(newOrder);
        }
        courseService.validateCourseIsInBuildingStatus(existingTask.getCourse().getStatus());
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
        courseService.validateCourseIsInBuildingStatus(task.getCourse().getStatus());
        taskValidator.validateForCreate(task);
        taskRepository.updateTaskOrderForInsert(task.getCourse().getId(), task.getOrder());
    }

    @Transactional
    public void deleteById(Long id) {
        if (id <= 0) {
            throw new TaskException("Id must be a positive value.", HttpStatus.BAD_REQUEST);
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskException("Task with id " + id + " not found.", HttpStatus.BAD_REQUEST));

        taskOrderService.adjustOrderForDelete(task);

        taskRepository.deleteById(id);
    }
}

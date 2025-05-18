package br.com.alura.AluraFake.task.service;

import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;

@Service
public class TaskOrderService {
    private final TaskRepository taskRepository;

    public TaskOrderService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void adjustOrderForUpdate(Task task, Integer newOrder) {
        if (newOrder == null)
            return;

        if (newOrder > task.getOrder()) {
            taskRepository.decrementOrderRange(task.getCourse().getId(), task.getOrder() + 1, newOrder);
        } else {
            taskRepository.incrementOrderRange(task.getCourse().getId(), newOrder, task.getOrder() - 1);
        }
    }

    public void adjustOrderForDelete(Task task) {
        taskRepository.decrementOrderRange(task.getCourse().getId(), task.getOrder() + 1, Integer.MAX_VALUE);
    }
}

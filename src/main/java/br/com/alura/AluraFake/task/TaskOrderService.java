package br.com.alura.AluraFake.task;

import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.task.models.Task;

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
}

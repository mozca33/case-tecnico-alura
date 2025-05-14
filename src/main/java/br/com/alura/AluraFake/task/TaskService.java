package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    public TaskDTO createTask(TaskDTO taskDTO) {
        switch (taskDTO.type()) {
            case OPEN_TEXT:
                return createOpenTextTask(taskDTO);
            default:
                throw new RuntimeException("Unknown task type.");
        }
    }

    public TaskDTO createOpenTextTask(TaskDTO taskDTO) {
        Task task = TaskMapper.toEntity(taskDTO);
        validateTask(task);

        taskRepository.updateTaskOrderForInsert(task.getCourseId(), task.getOrder());

        return TaskMapper.toDTO(taskRepository.save(task));
    }

    private void validateTask(Task newTask) {
        Course course = courseRepository.findById(newTask.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found."));

        if (course.getStatus() != Status.BUILDING) {
            throw new RuntimeException("Course is not in building status.");
        }
        List<Task> tasks = taskRepository.findByCourseId(newTask.getCourseId());
        boolean statementExists = tasks.stream()
                .anyMatch(task -> task.getStatement().equals(newTask.getStatement()));

        if (statementExists) {
            throw new RuntimeException("Task statement already exists.");
        }
    }
}

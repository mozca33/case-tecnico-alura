package br.com.alura.AluraFake.task;

public class TaskMapper {
    public static Task toEntity(TaskDTO taskDTO) {
        Task task = new Task(taskDTO.statement(), taskDTO.type(), taskDTO.order(), taskDTO.courseId());
        return task;
    }

    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(task.getId(), task.getCourseId(), task.getStatement(), task.getOrder(),
                task.getType());
    }
}

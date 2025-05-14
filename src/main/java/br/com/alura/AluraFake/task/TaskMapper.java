package br.com.alura.AluraFake.task;

public class TaskMapper {
    public static Task toEntity(TaskDTO taskDTO) {
        Task task = new Task();
        task.setCourseId(taskDTO.courseId());
        task.setStatement(taskDTO.statement());
        task.setOrder(taskDTO.order());
        task.setType(taskDTO.type());
        return task;
    }

    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(task.getId(), task.getCourseId(), task.getStatement(), task.getOrder(),
                task.getType());
    }
}

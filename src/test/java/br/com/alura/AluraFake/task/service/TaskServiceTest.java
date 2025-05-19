package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.validator.CourseValidator;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.validator.TaskValidator;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.course.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskOrderService taskOrderService;

    @Mock
    private TaskValidator taskValidator;

    @Mock
    private CourseValidator courseValidator;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        User user = new User("Name", "email@email.com", Role.INSTRUCTOR);
        course = new Course("Course Title", "Course Description", user);
        task = new Task(1L, "Statement", Type.OPEN_TEXT, 1, course);
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.createTask(task);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void findTasksByCourseId_shouldReturnTasksList() {
        when(taskRepository.findByCourseId(1L)).thenReturn(List.of(task));

        List<Task> result = taskService.findTasksByCourseId(1L);

        assertEquals(1, result.size());
        assertEquals(task.getId(), result.get(0).getId());
        verify(taskRepository, times(1)).findByCourseId(1L);
    }

    @Test
    void updateTask_shouldReturnExistingTask_whenNoChanges() {
        when(taskRepository.existsById(task.getId())).thenReturn(true);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task result = taskService.updateTask(task);

        assertNotNull(result);
        assertEquals(task, result);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_shouldThrowException_whenTaskNotFound() {
        when(taskRepository.existsById(task.getId())).thenReturn(false);

        TaskException exception = assertThrows(TaskException.class, () -> taskService.updateTask(task));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void findTasksByCourseId_shouldReturnTask() {

    }

    @Test
    void findTasksByCourseId_shouldThrowException_whenTaskNotFound() {

    }

    @Test
    void deleteTask_shouldDeleteTask_whenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> taskService.deleteById(1L));
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_shouldThrowException_whenTaskNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        TaskException exception = assertThrows(TaskException.class, () -> taskService.deleteById(99L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
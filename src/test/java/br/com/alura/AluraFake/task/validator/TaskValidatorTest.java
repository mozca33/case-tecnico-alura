package br.com.alura.AluraFake.task.validator;

import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.models.TaskOption;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskValidatorTest {

    private TaskRepository taskRepository;
    private TaskValidator validator;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        validator = new TaskValidator(taskRepository);
    }

    private Task createTask(Long id, Long courseId, int order, boolean singleChoice, boolean multipleChoice,
            List<TaskOption> options, String statement) {
        Task task = mock(Task.class);
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(courseId);
        when(task.getId()).thenReturn(id);
        when(task.getCourse()).thenReturn(course);
        when(task.getOrder()).thenReturn(order);
        when(task.isSingleChoice()).thenReturn(singleChoice);
        when(task.isMultipleChoice()).thenReturn(multipleChoice);
        when(task.getOptions()).thenReturn(options);
        when(task.getStatement()).thenReturn(statement);
        return task;
    }

    private TaskOption createOption(String text, boolean correct) {
        TaskOption option = mock(TaskOption.class);
        when(option.getTaskOption()).thenReturn(text);
        when(option.getCorrect()).thenReturn(correct);
        return option;
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenTaskLimitReached() {
        Task task = createTask(null, 1L, 1, false, false, null, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(5);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task limit reached"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenOrderIsNotSequential() {
        Task task = createTask(null, 1L, 2, false, false, null, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 1)).thenReturn(null);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task order is not sequential"));
    }

    @Test
    void validateForCreate_shouldThrowConflict_whenStatementAlreadyExists() {
        Task task = createTask(null, 1L, 1, false, false, null, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(true);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task statement already exists"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenSingleChoiceOptionsCountIsInvalid() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true));
        Task task = createTask(null, 1L, 1, true, false, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task must have between 2 and 5 options"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenMultipleChoiceOptionsCountIsInvalid() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true),
                createOption("B", false));
        Task task = createTask(null, 1L, 1, false, true, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task must have between 3 and 5 options"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenSingleChoiceHasInvalidCorrectOptionsCount() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true),
                createOption("B", true));
        Task task = createTask(null, 1L, 1, true, false, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task must have exactly 1 correct option"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenMultipleChoiceHasInvalidCorrectOptionsCount() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true),
                createOption("B", false),
                createOption("C", false));
        Task task = createTask(null, 1L, 1, false, true, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task must have between 2 and 4 correct options"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenOptionTextEqualsStatement() {
        List<TaskOption> options = Arrays.asList(
                createOption("statement", true),
                createOption("B", false));
        Task task = createTask(null, 1L, 1, true, false, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Option text cannot be the same as the task statement"));
    }

    @Test
    void validateForCreate_shouldThrowBadRequest_whenDuplicateOptionTextFound() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true),
                createOption("a", false));
        Task task = createTask(null, 1L, 1, true, false, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForCreate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Duplicate option text found"));
    }

    @Test
    void validateForUpdate_shouldThrowBadRequest_whenOrderIsNotSequential() {
        Task task = createTask(2L, 1L, 2, false, false, null, "statement");
        when(taskRepository.findTopByCourseIdAndOrderAndIdNot(1L, 1, 2L)).thenReturn(null);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForUpdate(task));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task order is not sequential"));
    }

    @Test
    void validateForUpdate_shouldThrowConflict_whenStatementAlreadyExists() {
        Task task = createTask(2L, 1L, 1, false, false, null, "statement");
        when(taskRepository.findTopByCourseIdAndOrderAndIdNot(1L, 0, 2L)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatementAndIdNot(1L, "statement", 2L)).thenReturn(true);

        TaskException ex = assertThrows(TaskException.class, () -> validator.validateForUpdate(task));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("Task statement already exists"));
    }

    @Test
    void validatePositiveId_shouldThrowBadRequest_whenIdIsZeroOrNegative() {
        TaskException ex1 = assertThrows(TaskException.class, () -> validator.validatePositiveId(0L));
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatus());
        assertTrue(ex1.getMessage().contains("Id should be a positive value"));

        TaskException ex2 = assertThrows(TaskException.class, () -> validator.validatePositiveId(-1L));
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatus());
        assertTrue(ex2.getMessage().contains("Id should be a positive value"));
    }

    @Test
    void validateForCreate_shouldPass_whenSingleChoiceIsValid() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true),
                createOption("B", false));
        Task task = createTask(null, 1L, 1, true, false, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateForCreate(task));
    }

    @Test
    void validateForCreate_shouldPass_whenMultipleChoiceIsValid() {
        List<TaskOption> options = Arrays.asList(
                createOption("A", true),
                createOption("B", true),
                createOption("C", false));
        Task task = createTask(null, 1L, 1, false, true, options, "statement");
        when(taskRepository.countByCourseId(1L)).thenReturn(0);
        when(taskRepository.findTopByCourseIdAndOrder(1L, 0)).thenReturn(mock(Task.class));
        when(taskRepository.existsByCourseIdAndStatement(1L, "statement")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateForCreate(task));
    }
}
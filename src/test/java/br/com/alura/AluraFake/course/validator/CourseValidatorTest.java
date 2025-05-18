package br.com.alura.AluraFake.course.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.task.models.Task;

class CourseValidatorTest {

    private CourseRepository courseRepository;
    private CourseValidator courseValidator;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseValidator = new CourseValidator(courseRepository);
    }

    @Test
    void validateCourseIsInBuildingStatus_withBuildingStatus_DoesNotThrow() {
        assertDoesNotThrow(() -> courseValidator.validateCourseIsInBuildingStatus(Status.BUILDING));
    }

    @Test
    void validateCourseIsInBuildingStatus_withNonBuildingStatus_throwsException() {
        CourseException ex = assertThrows(CourseException.class,
                () -> courseValidator.validateCourseIsInBuildingStatus(Status.PUBLISHED));
        assertEquals("Course is not in BUILDING status.", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void validateCourseExistsById_WhenCourseExists_DoesNotThrow() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> courseValidator.validateCourseExistsById(1L));
    }

    @Test
    void validateCourseExistsById_WhenCourseDoesNotExist_throwsException() {
        when(courseRepository.existsById(42L)).thenReturn(false);

        CourseException ex = assertThrows(CourseException.class, () -> courseValidator.validateCourseExistsById(42L));
        assertEquals("Course with id 42 not found.", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void validateForPublishing_withValidCourse_DoesNotThrow() {
        Course course = mock(Course.class);
        List<Task> tasks = List.of(
                createTask(true, false, false),
                createTask(false, true, false),
                createTask(false, false, true));

        when(course.getId()).thenReturn(10L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(course.getTasks()).thenReturn(tasks);
        when(courseRepository.existsById(10L)).thenReturn(true);

        assertDoesNotThrow(() -> courseValidator.validateForPublishing(course));
    }

    @Test
    void validateForPublishing_withNonExistingCourse_throwsException() {
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(100L);
        when(courseRepository.existsById(100L)).thenReturn(false);

        CourseException ex = assertThrows(CourseException.class, () -> courseValidator.validateForPublishing(course));
        assertEquals("Course with id 100 not found.", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void validateForPublishing_throwsException_whenWrongStatus() {
        Course course = mock(Course.class);
        List<Task> tasks = List.of(
                createTask(true, false, false),
                createTask(false, true, false),
                createTask(false, false, true));

        when(course.getId()).thenReturn(20L);
        when(course.getStatus()).thenReturn(Status.PUBLISHED);
        when(course.getTasks()).thenReturn(tasks);
        when(courseRepository.existsById(20L)).thenReturn(true);

        CourseException ex = assertThrows(CourseException.class, () -> courseValidator.validateForPublishing(course));
        assertEquals("Course is not in BUILDING status.", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void validateForPublishing_throwsException_whenMissingTaskTypes() {
        Course course = mock(Course.class);

        List<Task> tasks = List.of(
                createTask(true, false, false),
                createTask(false, true, false));

        when(course.getId()).thenReturn(30L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(course.getTasks()).thenReturn(tasks);
        when(courseRepository.existsById(30L)).thenReturn(true);

        CourseException ex = assertThrows(CourseException.class, () -> courseValidator.validateForPublishing(course));
        assertEquals("The course does not have all types of task.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateForPublishing_throwsException_whenEmptyTaskList() {
        Course course = mock(Course.class);

        when(course.getId()).thenReturn(40L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(course.getTasks()).thenReturn(List.of());
        when(courseRepository.existsById(40L)).thenReturn(true);

        CourseException ex = assertThrows(CourseException.class, () -> courseValidator.validateForPublishing(course));
        assertEquals("The course does not have all types of task.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateForPublishing_throwsException_whenNullTasksList() {
        Course course = mock(Course.class);

        when(course.getId()).thenReturn(41L);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(course.getTasks()).thenReturn(null);
        when(courseRepository.existsById(41L)).thenReturn(true);

        assertThrows(NullPointerException.class, () -> courseValidator.validateForPublishing(course));
    }

    private Task createTask(boolean openText, boolean singleChoice, boolean multipleChoice) {
        Task task = mock(Task.class);
        when(task.isOpenText()).thenReturn(openText);
        when(task.isSingleChoice()).thenReturn(singleChoice);
        when(task.isMultipleChoice()).thenReturn(multipleChoice);
        return task;
    }
}

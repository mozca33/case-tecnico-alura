package br.com.alura.AluraFake.course.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.validator.CourseValidator;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CourseServiceTest {

    private CourseRepository courseRepository;
    private CourseValidator courseValidator;
    private UserService userService;
    private CourseService courseService;

    @BeforeEach
    void setup() {
        courseRepository = mock(CourseRepository.class);
        courseValidator = mock(CourseValidator.class);
        userService = mock(UserService.class);
        courseService = new CourseService(courseRepository, courseValidator, userService);
    }

    @Test
    void createCourse_whenValid_shouldSaveAndReturnCourse() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Java Basics", "Intro to Java", instructor);

        when(courseRepository.existsByTitle("Java Basics")).thenReturn(false);
        doNothing().when(userService).validateUserIsInstructor(instructor);
        when(courseRepository.save(course)).thenReturn(course);

        Course created = courseService.createCourse(course);

        assertEquals(course, created);
        verify(courseRepository).existsByTitle("Java Basics");
        verify(userService).validateUserIsInstructor(instructor);
        verify(courseRepository).save(course);
    }

    @Test
    void createCourse_whenTitleExists_shouldThrowConflictException() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Java Basics", "Intro to Java", instructor);

        when(courseRepository.existsByTitle("Java Basics")).thenReturn(true);

        CourseException ex = assertThrows(CourseException.class, () -> courseService.createCourse(course));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertTrue(ex.getMessage().contains("already exists"));

        verify(courseRepository).existsByTitle("Java Basics");
        verify(userService, never()).validateUserIsInstructor(any());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void createCourse_whenUserValidatorFails_shouldPropagateException() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Java Basics", "Intro to Java", instructor);

        when(courseRepository.existsByTitle("Java Basics")).thenReturn(false);
        doThrow(new CourseException("Invalid instructor", HttpStatus.BAD_REQUEST))
                .when(userService).validateUserIsInstructor(instructor);

        CourseException ex = assertThrows(CourseException.class, () -> courseService.createCourse(course));
        assertEquals("Invalid instructor", ex.getMessage());

        verify(courseRepository).existsByTitle("Java Basics");
        verify(userService).validateUserIsInstructor(instructor);
        verify(courseRepository, never()).save(any());
    }

    @Test
    void getById_whenCourseExists_shouldReturnCourse() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Title", "Desc", instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getById(1L);

        assertEquals(course, result);
        verify(courseRepository).findById(1L);
    }

    @Test
    void getById_whenIdNotPositive_shouldThrowBadRequest() {
        TaskException ex = assertThrows(TaskException.class, () -> courseService.getById(0L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("must be a positive"));
    }

    @Test
    void getById_whenCourseDoesNotExist_shouldThrowNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        CourseException ex = assertThrows(CourseException.class, () -> courseService.getById(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));

        verify(courseRepository).findById(99L);
    }

    @Test
    void getAllCourses_whenCoursesExist_shouldReturnListOfCourses() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course1 = new Course("Title1", "Desc1", instructor);
        Course course2 = new Course("Title2", "Desc2", instructor);

        when(courseRepository.findAll()).thenReturn(List.of(course1, course2));

        List<Course> all = courseService.getAllCourses();

        assertEquals(2, all.size());
        assertTrue(all.contains(course1));
        assertTrue(all.contains(course2));

        verify(courseRepository).findAll();
    }

    @Test
    void getAllCourses_whenNoCourses_shouldReturnEmptyList() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<Course> all = courseService.getAllCourses();

        assertTrue(all.isEmpty());
        verify(courseRepository).findAll();
    }

    @Test
    void publishCourse_whenCourseExistsAndValid_shouldSetStatusPublishedAndSave() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Title", "Desc", instructor);
        course.setStatus(Status.BUILDING);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseValidator).validateForPublishing(course);
        when(courseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Course published = courseService.publishCourse(1L);

        assertEquals(Status.PUBLISHED, published.getStatus());
        assertNotNull(published.getPublishedAt());
        verify(courseValidator).validateForPublishing(course);
        verify(courseRepository).save(course);
    }

    @Test
    void publishCourse_whenCourseDoesNotExist_shouldThrowNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        CourseException ex = assertThrows(CourseException.class, () -> courseService.publishCourse(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().contains("not found"));

        verify(courseRepository).findById(99L);
        verify(courseValidator, never()).validateForPublishing(any());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void publishCourse_whenValidationFails_shouldPropagateException() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course course = new Course("Title", "Desc", instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doThrow(new CourseException("Invalid for publish", HttpStatus.BAD_REQUEST))
                .when(courseValidator).validateForPublishing(course);

        CourseException ex = assertThrows(CourseException.class, () -> courseService.publishCourse(1L));
        assertEquals("Invalid for publish", ex.getMessage());

        verify(courseValidator).validateForPublishing(course);
        verify(courseRepository, never()).save(any());
    }

    @Test
    void updateCourse_whenValid_shouldUpdateFieldsAndSave() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course existingCourse = new Course("Old Title", "Old Desc", instructor);
        existingCourse.setStatus(Status.BUILDING);

        User newInstructor = mock(User.class);
        when(newInstructor.isInstructor()).thenReturn(true);

        Course update = new Course("New Title", "New Desc", newInstructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.existsByTitle("New Title")).thenReturn(false);
        doNothing().when(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);
        doNothing().when(userService).validateUserIsInstructor(newInstructor);
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);

        courseService.updateCourse(1L, update);

        assertEquals("New Title", existingCourse.getTitle());
        assertEquals("New Desc", existingCourse.getDescription());
        assertEquals(newInstructor, existingCourse.getInstructor());
        verify(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);
        verify(userService).validateUserIsInstructor(newInstructor);
        verify(courseRepository).save(existingCourse);
    }

    @Test
    void updateCourse_whenCourseNotFound_shouldThrowNotFoundException() {
        Course update = mock(Course.class);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CourseException ex = assertThrows(CourseException.class, () -> courseService.updateCourse(1L, update));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(courseRepository).findById(1L);
    }

    @Test
    void updateCourse_whenTitleAlreadyExists_shouldThrowConflictException() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course existingCourse = new Course("Old Title", "Old Desc", instructor);
        existingCourse.setStatus(Status.BUILDING);

        Course update = new Course("Existing Title", "New Desc", instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        doNothing().when(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);
        when(courseRepository.existsByTitle("Existing Title")).thenReturn(true);

        CourseException ex = assertThrows(CourseException.class, () -> courseService.updateCourse(1L, update));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(courseRepository).existsByTitle("Existing Title");
    }

    @Test
    void deleteCourse_whenCourseExistsAndValid_shouldDelete() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        Course existingCourse = new Course("Title", "Desc", instructor);
        existingCourse.setStatus(Status.BUILDING);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        doNothing().when(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);

        courseService.deleteCourse(1L);

        verify(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);
        verify(courseRepository).deleteById(1L);
    }

    @Test
    void deleteCourse_whenCourseNotFound_shouldThrowNotFoundException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CourseException ex = assertThrows(CourseException.class, () -> courseService.deleteCourse(1L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(courseRepository).findById(1L);
        verify(courseRepository, never()).deleteById(any());
    }

    @Test
    void validateCourseIsInBuildingStatus_shouldDelegateToValidator() {
        doNothing().when(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);

        courseService.validateCourseIsInBuildingStatus(Status.BUILDING);

        verify(courseValidator).validateCourseIsInBuildingStatus(Status.BUILDING);
    }
}

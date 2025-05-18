package br.com.alura.AluraFake.course.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import br.com.alura.AluraFake.course.dto.CourseDTO;
import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;

class CourseMapperTest {

    private UserService userService;
    private CourseRepository courseRepository;
    private CourseMapper courseMapper;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        courseRepository = mock(CourseRepository.class);
        courseMapper = new CourseMapper(userService);
    }

    @Test
    void toEntityById_whenCourseFound_returnsCourse() {
        Course course = mock(Course.class);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = CourseMapper.toEntity(1L, courseRepository);

        assertSame(course, result);
        verify(courseRepository).findById(1L);
    }

    @Test
    void toEntityById_whenCourseNotFound_throwsCourseException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        CourseException exception = assertThrows(CourseException.class,
                () -> CourseMapper.toEntity(1L, courseRepository));

        assertEquals("Course 1 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void toEntityFromDTO_whenInstructorFoundAndIsInstructor_createsCourse() {
        User instructor = mock(User.class);
        when(userService.findByEmail("prof@example.com")).thenReturn(Optional.of(instructor));
        when(instructor.isInstructor()).thenReturn(true);

        CourseDTO dto = new CourseDTO(null, "Java 101", "Course description", "prof@example.com");

        Course course = courseMapper.toEntity(dto);

        assertEquals(dto.title(), course.getTitle());
        assertEquals(dto.description(), course.getDescription());
        assertSame(instructor, course.getInstructor());
    }

    @Test
    void toEntityFromDTO_whenInstructorNotFound_throwsCourseException() {
        when(userService.findByEmail("prof@example.com")).thenReturn(Optional.empty());

        CourseDTO dto = new CourseDTO(null, "Java 101", "Course description", "prof@example.com");

        CourseException exception = assertThrows(CourseException.class, () -> courseMapper.toEntity(dto));

        assertEquals("Instructor with email prof@example.com not found", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void toEntityFromDTO_whenUserIsNotInstructor_throwsCourseException() {
        User user = mock(User.class);
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(user.isInstructor()).thenReturn(false);

        CourseDTO dto = new CourseDTO(null, "Java 101", "Course description", "user@example.com");

        CourseException exception = assertThrows(CourseException.class, () -> courseMapper.toEntity(dto));

        assertEquals("User is not an instructor.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void toDTOFromCourse_returnsCorrectDTO() {
        User instructor = mock(User.class);
        when(instructor.getEmail()).thenReturn("prof@example.com");

        Course course = mock(Course.class);
        when(course.getId()).thenReturn(123L);
        when(course.getTitle()).thenReturn("Java 101");
        when(course.getDescription()).thenReturn("Description");
        when(course.getInstructor()).thenReturn(instructor);

        CourseDTO dto = courseMapper.toDTO(course);

        assertEquals(123L, dto.id());
        assertEquals("Java 101", dto.title());
        assertEquals("Description", dto.description());
        assertEquals("prof@example.com", dto.emailInstructor());
    }

    @Test
    void toDTOFromCourseList_mapsCorrectly() {
        Course course1 = mock(Course.class);
        when(course1.getId()).thenReturn(1L);
        when(course1.getTitle()).thenReturn("Course 1");
        when(course1.getDescription()).thenReturn("Desc 1");
        when(course1.getStatus()).thenReturn(br.com.alura.AluraFake.course.enums.Status.BUILDING);

        Course course2 = mock(Course.class);
        when(course2.getId()).thenReturn(2L);
        when(course2.getTitle()).thenReturn("Course 2");
        when(course2.getDescription()).thenReturn("Desc 2");
        when(course2.getStatus()).thenReturn(br.com.alura.AluraFake.course.enums.Status.PUBLISHED);

        List<CourseListItemDTO> list = courseMapper.toDTO(List.of(course1, course2));

        assertEquals(2, list.size());

        CourseListItemDTO dto1 = list.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Course 1", dto1.getTitle());
        assertEquals("Desc 1", dto1.getDescription());
        assertEquals(br.com.alura.AluraFake.course.enums.Status.BUILDING, dto1.getStatus());

        CourseListItemDTO dto2 = list.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Course 2", dto2.getTitle());
        assertEquals("Desc 2", dto2.getDescription());
        assertEquals(br.com.alura.AluraFake.course.enums.Status.PUBLISHED, dto2.getStatus());
    }

    @Test
    void toDTOFromCourseList_whenEmpty_returnsEmptyList() {
        List<CourseListItemDTO> list = courseMapper.toDTO(List.of());
        assertTrue(list.isEmpty());
    }
}

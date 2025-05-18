package br.com.alura.AluraFake.task.mapper;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.task.dto.BaseTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskMapperTest {

    private TaskMapper taskMapper;

    private CourseService courseService;

    private Task task;
    private Course course;
    private BaseTaskDTO dto;

    @BeforeEach
    void setUp() {
        courseService = mock(CourseService.class);
        taskMapper = new TaskMapper(courseService);
        User user = new User("Name", "email@email.com", Role.INSTRUCTOR);
        course = new Course("Course Title", "Course Description", user);
        dto = new OpenTextTaskDTO(1L, course.getId(), "Statement", 1, Type.OPEN_TEXT);
        task = new Task(1L, "Statement", Type.OPEN_TEXT, 1, course);
    }

    @Test
    void toDto_shouldMapTaskToTaskDto() {
        BaseTaskDTO dto = taskMapper.toDTO(task);

        assertNotNull(dto);
        assertEquals(task.getId(), dto.id());
        assertEquals(task.getStatement(), dto.statement());
        assertEquals(task.getType(), dto.type());
        assertEquals(task.getOrder(), dto.order());
        assertEquals(task.getCourse().getId(), dto.courseId());
    }

    @Test
    void toEntity_shouldMapTaskDtoToTask() {
        when(courseService.getById(dto.courseId())).thenReturn(course);
        Task entity = taskMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.statement(), entity.getStatement());
        assertEquals(dto.type(), entity.getType());
        assertEquals(dto.order(), entity.getOrder());
        assertEquals(course, entity.getCourse());
    }

    @Test
    void toDto_shouldReturnNull_whenTaskIsNull() {
        assertNull(taskMapper.toDTO((Task) null));
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(taskMapper.toEntity(null, (BaseTaskDTO) null));
    }

    @Test
    void toEntity_withId_shouldMapTaskDtoToTaskWithId() {
        when(courseService.getById(dto.courseId())).thenReturn(course);
        Task entity = taskMapper.toEntity(99L, dto);

        assertNotNull(entity);
        assertEquals(99L, entity.getId());
        assertEquals(dto.statement(), entity.getStatement());
        assertEquals(dto.type(), entity.getType());
        assertEquals(dto.order(), entity.getOrder());
        assertEquals(course, entity.getCourse());
    }

    @Test
    void toEntity_withId_shouldReturnNull_whenDtoIsNull() {
        assertNull(taskMapper.toEntity(1L, null));
    }

    @Test
    void toDTO_shouldMapListOfTasksToListOfDTOs() {
        Task task2 = new Task(2L, "Statement 2", Type.OPEN_TEXT, 2, course);
        var tasks = java.util.List.of(task, task2);

        var dtos = taskMapper.toDTO(tasks);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(task.getId(), dtos.get(0).id());
        assertEquals(task2.getId(), dtos.get(1).id());
    }

    @Test
    void toPartialEntity_shouldMapTaskPatchDTOWIthoutCourseId() {
        var patchDto = mock(br.com.alura.AluraFake.task.dto.TaskPatchDTO.class);
        when(patchDto.courseId()).thenReturn(null);
        when(patchDto.toPartialEntity(1L)).thenReturn(task);

        Task result = taskMapper.toPartialEntity(1L, patchDto);

        assertNotNull(result);
        assertEquals(task, result);
    }
}
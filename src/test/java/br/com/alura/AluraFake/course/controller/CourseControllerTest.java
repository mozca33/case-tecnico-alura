package br.com.alura.AluraFake.course.controller;

import br.com.alura.AluraFake.course.dto.CourseDTO;
import br.com.alura.AluraFake.course.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.mapper.CourseMapper;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.service.CourseService;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CourseService courseService;

        @MockBean
        private UserService userService;

        @MockBean
        private CourseMapper courseMapper;

        @Autowired
        private ObjectMapper objectMapper;

        private User validUser;

        @BeforeEach
        void setup() {
                validUser = new User("Rafael", "rafaelfelipe@gmail.com", Role.INSTRUCTOR);
        }

        @Test
        void createCourse_shouldReturnCreated_whenValidData() throws Exception {
                CourseDTO inputDTO = new CourseDTO(null, "Java Basics", "A beginner course", "instructor@email.com");
                User user = new User("Instructor", "instructor@email.com", Role.INSTRUCTOR);
                Course courseEntity = new Course("Java Basics", "A beginner course", user);

                CourseDTO responseDTO = new CourseDTO(1L, "Java Basics", "A beginner course", "instructor@email.com");

                when(courseMapper.toEntity(inputDTO)).thenReturn(courseEntity);
                when(courseService.createCourse(any(Course.class))).thenReturn(courseEntity);
                when(courseMapper.toDTO(courseEntity)).thenReturn(responseDTO);

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Java Basics"))
                                .andExpect(jsonPath("$.description").isNotEmpty())
                                .andExpect(jsonPath("$.emailInstructor").value("instructor@email.com"));
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenCourseTitleInvalid() throws Exception {
                CourseDTO invalidDTO = new CourseDTO(null, "", "123", "not-an-email");

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").isNotEmpty())
                                .andExpect(jsonPath("$[0].message").isNotEmpty());
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenMalformedJson() throws Exception {
                String malformedJson = """
                                {
                                        "title": "example",
                                        "description": "this is an example",
                                        "emailInstructor": "valid@gmail.com"

                                """;
                ;

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(malformedJson))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenInvalidCourse() throws Exception {
                CourseDTO invalidDTO = new CourseDTO(null, "", "123", "not-an-email");

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.size()").value(4))
                                .andExpect(jsonPath("$[0].field").isNotEmpty())
                                .andExpect(jsonPath("$[0].message").isNotEmpty())
                                .andExpect(jsonPath("$[1].field").isNotEmpty())
                                .andExpect(jsonPath("$[1].message").isNotEmpty())
                                .andExpect(jsonPath("$[2].field").isNotEmpty())
                                .andExpect(jsonPath("$[2].message").isNotEmpty());
        }

        @Test
        void createCourse_shouldReturnForbidden_whenUserIsNotInstructor() throws Exception {
                CourseDTO dto = new CourseDTO(null, "Title", "Description", "student@email.com");
                User user = mock(User.class);

                when(userService.findByEmail(dto.emailInstructor())).thenReturn(Optional.of(user));
                when(user.isInstructor()).thenReturn(false);
                when(courseMapper.toEntity(dto))
                                .thenThrow(new CourseException("User is not an instructor", HttpStatus.FORBIDDEN));

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.message").value("User is not an instructor"));
        }

        @Test
        void createCourse_shouldReturnNotFound_whenUserNotFound() throws Exception {
                CourseDTO inputDTO = new CourseDTO(null, "Java Basics", "A beginner course", "missinguser@email.com");
                User user = new User("fake", inputDTO.emailInstructor(), Role.INSTRUCTOR);
                Course course = new Course(inputDTO.title(), inputDTO.description(), user);

                when(courseMapper.toEntity(inputDTO)).thenReturn(course);

                when(courseService.createCourse(any(Course.class)))
                                .thenThrow(new CourseException("User not found", HttpStatus.NOT_FOUND));

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenTitleTooLong() throws Exception {

                String longTitle = "A".repeat(100);
                CourseDTO dto = new CourseDTO(null, longTitle, "Descrição válida", "valid@email.com");

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").value("title"))
                                .andExpect(jsonPath("$[0].message").value("Title should have from 4 to 80 characters"));
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenDescriptionTooLong() throws Exception {

                String longDescription = "D".repeat(256);
                CourseDTO dto = new CourseDTO(null, "Título válido", longDescription, "valid@email.com");

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").value("description"))
                                .andExpect(jsonPath("$[0].message")
                                                .value("Description should have from 4 to 255 characters"));
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenEmailTooLong() throws Exception {

                String longEmail = "a".repeat(290) + "@email.com";
                CourseDTO dto = new CourseDTO(null, "Título válido", "Descrição válida", longEmail);

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").value("emailInstructor"));
        }

        @Test
        void createCourse_shouldReturnBadRequest_whenAllFieldsEmpty() throws Exception {
                CourseDTO dto = new CourseDTO(null, "", "", "");

                mockMvc.perform(post("/course/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.length()").value(5))
                                .andExpect(jsonPath("$[0].field").isNotEmpty())
                                .andExpect(jsonPath("$[0].message").isNotEmpty())
                                .andExpect(jsonPath("$[1].field").isNotEmpty())
                                .andExpect(jsonPath("$[1].message").isNotEmpty())
                                .andExpect(jsonPath("$[2].field").isNotEmpty())
                                .andExpect(jsonPath("$[2].message").isNotEmpty())
                                .andExpect(jsonPath("$[3].field").isNotEmpty())
                                .andExpect(jsonPath("$[3].message").isNotEmpty())
                                .andExpect(jsonPath("$[4].field").isNotEmpty())
                                .andExpect(jsonPath("$[4].message").isNotEmpty());
        }

        @Test
        void getAllCourses_shouldReturnOk_whenValidData() throws Exception {
                CourseListItemDTO course1 = new CourseListItemDTO(new Course("Java", "Intro course", validUser));
                CourseListItemDTO course2 = new CourseListItemDTO(new Course("Python", "Another course", validUser));

                when(courseMapper.toDTO(Mockito.<List<Course>>any())).thenReturn(List.of(course1, course2));

                mockMvc.perform(get("/course/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(2))
                                .andExpect(jsonPath("$[0].title").value("Java"))
                                .andExpect(jsonPath("$[0].description").value("Intro course"))
                                .andExpect(jsonPath("$[0].status").value("BUILDING"))
                                .andExpect(jsonPath("$[1].title").value("Python"))
                                .andExpect(jsonPath("$[1].description").value("Another course"))
                                .andExpect(jsonPath("$[1].status").value("BUILDING"));
        }

        @Test
        void getAllCourses_shouldReturnEmptyList_whenNoCourses() throws Exception {
                when(courseMapper.toDTO(Mockito.<List<Course>>any())).thenReturn(List.of());

                mockMvc.perform(get("/course/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.size()").value(0));
        }

        @Test
        void getCourseById_shouldReturnOk_whenCourseExists() throws Exception {
                Course course = new Course("Java", "Intro course", validUser);
                CourseDTO dto = new CourseDTO(1L, "Java", "Intro course", "user@email.com");

                Mockito.when(courseService.getById(1L)).thenReturn(course);
                Mockito.when(courseMapper.toDTO(course)).thenReturn(dto);

                mockMvc.perform(get("/course/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        void getCourseById_shouldReturnNotFound_whenCourseNotFound() throws Exception {
                Long nonExistentId = 999L;

                when(courseService.getById(nonExistentId))
                                .thenThrow(new CourseException("Course with id " + nonExistentId + " not found.",
                                                HttpStatus.NOT_FOUND));

                mockMvc.perform(get("/course/{id}", nonExistentId))
                                .andExpect(status().isNotFound());
        }

        @Test
        void getCourseById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
                mockMvc.perform(get("/course/-1"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void publishCourse_shouldReturnOk_whenCourseCanBePublished() throws Exception {
                Long courseId = 1L;
                Course course = new Course("Java", "Intro course", validUser);

                course.setStatus(Status.PUBLISHED);
                course.setPublishedAt(LocalDateTime.now());

                CourseDTO courseDTO = new CourseDTO(courseId, course.getTitle(), course.getDescription(),
                                course.getInstructor().getEmail());

                when(courseService.publishCourse(courseId)).thenReturn(course);
                when(courseMapper.toDTO(course)).thenReturn(courseDTO);

                mockMvc.perform(post("/course/{id}/publish", courseId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(courseId))
                                .andExpect(jsonPath("$.title").value("Java"))
                                .andExpect(jsonPath("$.description").value("Intro course"))
                                .andExpect(jsonPath("$.emailInstructor").value(course.getInstructor().getEmail()));
        }

        @Test
        void publishCourse_shouldReturnBadRequest_whenCourseAlreadyPublished() throws Exception {
                Long courseId = 1L;
                Course course = new Course("Java", "Intro course", validUser);
                course.setStatus(Status.PUBLISHED);

                when(courseService.publishCourse(courseId))
                                .thenThrow(new CourseException("Course already published", HttpStatus.BAD_REQUEST));

                mockMvc.perform(post("/course/{id}/publish", courseId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void publishCourse_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
                mockMvc.perform(post("/course/{id}/publish", 0L)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void publishCourse_shouldReturnNotFound_whenCourseNotFound() throws Exception {
                Long nonExistentId = 999L;

                when(courseService.publishCourse(nonExistentId))
                                .thenThrow(new CourseException("Course with id " + nonExistentId + " not found.",
                                                HttpStatus.NOT_FOUND));

                mockMvc.perform(post("/course/{id}/publish", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}

package br.com.alura.AluraFake.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.alura.AluraFake.task.dto.BaseTaskDTO;
import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.TaskOptionDTO;
import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.mapper.TaskOptionMapper;
import br.com.alura.AluraFake.task.models.Task;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.task.service.TaskService;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskService taskService;

        @MockBean
        private TaskMapper taskMapper;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void createMultipleChoiceTask_shouldReturnCreatedTask_whenRequestIsValid() throws Exception {
                MultipleChoiceTaskDTO requestDTO = new MultipleChoiceTaskDTO(
                                null,
                                1L,
                                "What is Java?",
                                1,
                                null,
                                List.of(
                                                new TaskOptionDTO("Option 1", true),
                                                new TaskOptionDTO("Option 2", true),
                                                new TaskOptionDTO("Option 3", false)));

                Task taskEntity = new Task(1L, "What is Java?", Type.MULTIPLE_CHOICE, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                taskEntity.setOptions(TaskOptionMapper.toEntityList(requestDTO.options()));

                when(taskMapper.toEntity(any(BaseTaskDTO.class))).thenReturn(taskEntity);
                when(taskService.createTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new MultipleChoiceTaskDTO(
                                1L, 1L, "What is Java?", 1, Type.MULTIPLE_CHOICE, requestDTO.options());

                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/tasks/multiplechoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.statement").value("What is Java?"))
                                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"));
        }

        @Test
        void getTasksFromCourse_shouldReturnTasksList_whenCourseExists() throws Exception {
                Long courseId = 1L;
                Task task = new Task(1L, "Statement", Type.OPEN_TEXT, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));
                List<Task> tasks = List.of(task);

                BaseTaskDTO dto = new OpenTextTaskDTO(1L, courseId, "Statement", 1, Type.OPEN_TEXT);

                when(taskService.findTasksByCourseId(courseId)).thenReturn(tasks);
                when(taskMapper.toDTO(any(List.class))).thenReturn(List.of(dto));

                mockMvc.perform(get("/tasks/course/{id}", courseId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].statement").value("Statement"));
        }

        @Test
        void getTasksFromCourse_shouldReturnEmptyList_whenCourseHasNoTasks() throws Exception {
                Long courseId = 2L;
                when(taskService.findTasksByCourseId(courseId)).thenReturn(Collections.emptyList());
                when(taskMapper.toDTO(any(List.class))).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/tasks/course/{id}", courseId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void getTasksFromCourse_shouldReturnNotFound_whenCourseDoesNotExist() throws Exception {
                Long courseId = 999L;
                when(taskService.findTasksByCourseId(courseId))
                                .thenThrow(new TaskException("Course with id " + courseId + " not found.",
                                                org.springframework.http.HttpStatus.NOT_FOUND));

                mockMvc.perform(get("/tasks/course/{id}", courseId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Course with id " + courseId + " not found."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
                ;
        }

        @Test
        void createOpenTextTask_shouldReturnCreatedTask_whenValidRequest() throws Exception {
                OpenTextTaskDTO requestDTO = new OpenTextTaskDTO(null, 1L, "Describe Java", 1, null);
                Task taskEntity = new Task(1L, "Describe Java", Type.OPEN_TEXT, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toEntity(any(OpenTextTaskDTO.class))).thenReturn(taskEntity);
                when(taskService.createTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new OpenTextTaskDTO(1L, 1L, "Describe Java", 1, Type.OPEN_TEXT);
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/tasks/opentext")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.statement").value("Describe Java"))
                                .andExpect(jsonPath("$.type").value("OPEN_TEXT"));
        }

        @Test
        void createOpenTextTask_shouldReturnBadRequest_whenInvalidInput() throws Exception {
                OpenTextTaskDTO requestDTO = new OpenTextTaskDTO(null, null, "", 1, null);

                mockMvc.perform(post("/tasks/opentext")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void createSingleChoiceTask_shouldReturnCreatedTask_whenValidRequest() throws Exception {
                SingleChoiceTaskDTO requestDTO = new SingleChoiceTaskDTO(null, 1L, "Pick one", 1, null,
                                List.of(
                                                new TaskOptionDTO("Option 1", true),
                                                new TaskOptionDTO("Option 2", false),
                                                new TaskOptionDTO("Option 3", false)));

                Task taskEntity = new Task(1L, "Pick one", Type.SINGLE_CHOICE, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));
                taskEntity.setOptions(TaskOptionMapper.toEntityList(requestDTO.options()));

                when(taskMapper.toEntity(any(BaseTaskDTO.class))).thenReturn(taskEntity);
                when(taskService.createTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new SingleChoiceTaskDTO(1L, 1L, "Pick one", 1, Type.SINGLE_CHOICE,
                                requestDTO.options());
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/tasks/singlechoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.statement").value("Pick one"))
                                .andExpect(jsonPath("$.type").value("SINGLE_CHOICE"));
        }

        @Test
        void createSingleChoiceTask_shouldReturnBadRequest_whenInvalidInput() throws Exception {
                SingleChoiceTaskDTO requestDTO = new SingleChoiceTaskDTO(null, null, "", 1, null, null);

                mockMvc.perform(post("/tasks/singlechoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void createMultipleChoiceTask_shouldReturnBadRequest_whenInvalidInput() throws Exception {
                MultipleChoiceTaskDTO requestDTO = new MultipleChoiceTaskDTO(null, null, "", 1, null, null);

                mockMvc.perform(post("/tasks/multiplechoice")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateOpenTextTask_shouldReturnUpdatedTask_whenTaskExistsAndValidRequest() throws Exception {
                Long taskId = 1L;
                OpenTextTaskDTO requestDTO = new OpenTextTaskDTO(null, 1L, "Updated", 1, null);
                Task taskEntity = new Task(taskId, "Updated", Type.OPEN_TEXT, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toEntity(eq(taskId), any(OpenTextTaskDTO.class))).thenReturn(taskEntity);
                when(taskService.updateTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new OpenTextTaskDTO(taskId, 1L, "Updated", 1, Type.OPEN_TEXT);
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(put("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.statement").value("Updated"));
        }

        @Test
        void updateOpenTextTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                OpenTextTaskDTO requestDTO = new OpenTextTaskDTO(null, 1L, "Updated", 1, null);

                when(taskMapper.toEntity(eq(taskId), any(OpenTextTaskDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found.",
                                                org.springframework.http.HttpStatus.NOT_FOUND));

                mockMvc.perform(put("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
        }

        @Test
        void updateOpenTextTask_shouldReturnBadRequest_whenInvalidInput() throws Exception {
                Long taskId = 1L;
                OpenTextTaskDTO requestDTO = new OpenTextTaskDTO(null, null, "", 1, null);

                mockMvc.perform(put("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateSingleChoiceTask_shouldReturnUpdatedTask_whenTaskExistsAndValidRequest() throws Exception {
                Long taskId = 1L;
                SingleChoiceTaskDTO requestDTO = new SingleChoiceTaskDTO(null, 1L, "Updated", 1, null,
                                List.of(
                                                new TaskOptionDTO("OPT1", true),
                                                new TaskOptionDTO("OPT2", false)));

                Task taskEntity = new Task(taskId, "Updated", Type.SINGLE_CHOICE, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toEntity(eq(taskId), any(SingleChoiceTaskDTO.class))).thenReturn(taskEntity);
                when(taskService.updateTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new SingleChoiceTaskDTO(taskId, 1L, "Updated", 1, Type.SINGLE_CHOICE,
                                requestDTO.options());
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(put("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.statement").value("Updated"));
        }

        @Test
        void updateSingleChoiceTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                SingleChoiceTaskDTO requestDTO = new SingleChoiceTaskDTO(null, 1L, "Updated", 1, null,
                                List.of(
                                                new TaskOptionDTO("OPT1", true),
                                                new TaskOptionDTO("OPT2", false)));

                when(taskMapper.toEntity(eq(taskId), any(SingleChoiceTaskDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found.",
                                                org.springframework.http.HttpStatus.NOT_FOUND));

                mockMvc.perform(put("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
                ;
        }

        @Test
        void updateSingleChoiceTask_shouldReturnBadRequest_whenInvalidInput() throws Exception {
                Long taskId = 1L;
                SingleChoiceTaskDTO requestDTO = new SingleChoiceTaskDTO(null, null, "", 1, null, null);

                mockMvc.perform(put("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateMultipleChoiceTask_shouldReturnUpdatedTask_whenTaskExistsAndValidRequest() throws Exception {
                Long taskId = 1L;
                MultipleChoiceTaskDTO requestDTO = new MultipleChoiceTaskDTO(null, 1L, "Updated", 1, null,
                                List.of(
                                                new TaskOptionDTO("OPT1", true),
                                                new TaskOptionDTO("OPT2", false),
                                                new TaskOptionDTO("OPT3", true)));

                Task taskEntity = new Task(taskId, "Updated", Type.MULTIPLE_CHOICE, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toEntity(eq(taskId), any(MultipleChoiceTaskDTO.class))).thenReturn(taskEntity);
                when(taskService.updateTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new MultipleChoiceTaskDTO(taskId, 1L, "Updated", 1, Type.MULTIPLE_CHOICE,
                                requestDTO.options());
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(put("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.statement").value("Updated"));
        }

        @Test
        void updateMultipleChoiceTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                MultipleChoiceTaskDTO requestDTO = new MultipleChoiceTaskDTO(null, 1L, "Updated", 1, null,
                                List.of(
                                                new TaskOptionDTO("OPT1", true),
                                                new TaskOptionDTO("OPT2", false),
                                                new TaskOptionDTO("OPT3", true)));

                when(taskMapper.toEntity(eq(taskId), any(MultipleChoiceTaskDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found.",
                                                org.springframework.http.HttpStatus.NOT_FOUND));

                mockMvc.perform(put("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
                ;
        }

        @Test
        void updateMultipleChoiceTask_shouldReturnBadRequest_whenInvalidInput() throws Exception {
                Long taskId = 1L;
                MultipleChoiceTaskDTO requestDTO = new MultipleChoiceTaskDTO(null, null, "", 1, null, null);

                mockMvc.perform(put("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateOpenTextTask_shouldReturnBadRequest_whenNullRequestBody() throws Exception {
                Long taskId = 1L;
                mockMvc.perform(put("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("null"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateSingleChoiceTask_shouldReturnBadRequest_whenNullRequestBody() throws Exception {
                Long taskId = 1L;
                mockMvc.perform(put("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("null"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void updateMultipleChoiceTask_shouldReturnBadRequest_whenNullRequestBody() throws Exception {
                Long taskId = 1L;
                mockMvc.perform(put("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("null"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void patchOpenTextTask_shouldReturnPatchedTask_whenTaskExistsAndValidPatch() throws Exception {
                Long taskId = 1L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                Task taskEntity = new Task(taskId, "Patched", Type.OPEN_TEXT, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(taskEntity);
                when(taskService.updateTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new OpenTextTaskDTO(taskId, 1L, "Patched", 1, Type.OPEN_TEXT);
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(patch("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.statement").value("Patched"));
        }

        @Test
        void patchOpenTextTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found", HttpStatus.NOT_FOUND));

                mockMvc.perform(patch("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found"))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
        }

        @Test
        void patchOpenTextTask_shouldReturnBadRequest_whenInvalidPatch() throws Exception {
                Long taskId = 1L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("", null, null, null, null);

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Statement cannot be blank.", HttpStatus.BAD_REQUEST));

                mockMvc.perform(patch("/tasks/opentext/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Statement cannot be blank."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.toString()));
        }

        @Test
        void patchSingleChoiceTask_shouldReturnPatchedTask_whenTaskExistsAndValidPatch() throws Exception {
                Long taskId = 1L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                Task taskEntity = new Task(taskId, "Patched", Type.SINGLE_CHOICE, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(taskEntity);
                when(taskService.updateTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new SingleChoiceTaskDTO(taskId, 1L, "Patched", 1, Type.SINGLE_CHOICE, null);
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(patch("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.statement").value("Patched"));
        }

        @Test
        void patchSingleChoiceTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found", HttpStatus.NOT_FOUND));

                mockMvc.perform(patch("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found"))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
        }

        @Test
        void patchSingleChoiceTask_shouldReturnBadRequest_whenInvalidPatch() throws Exception {
                Long taskId = 1L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("", null, null, null, null);

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Statement cannot be blank", HttpStatus.BAD_REQUEST));

                mockMvc.perform(patch("/tasks/singlechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void patchMultipleChoiceTask_shouldReturnPatchedTask_whenTaskExistsAndValidPatch() throws Exception {
                Long taskId = 1L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                Task taskEntity = new Task(taskId, "Patched", Type.MULTIPLE_CHOICE, 1,
                                new Course("title", "description",
                                                new User("Name", "name@email.com", Role.INSTRUCTOR)));

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(taskEntity);
                when(taskService.updateTask(any(Task.class))).thenReturn(taskEntity);

                BaseTaskDTO responseDTO = new MultipleChoiceTaskDTO(taskId, 1L, "Patched", 1, Type.MULTIPLE_CHOICE,
                                null);
                when(taskMapper.toDTO(any(Task.class))).thenReturn(responseDTO);

                mockMvc.perform(patch("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(taskId))
                                .andExpect(jsonPath("$.statement").value("Patched"));
        }

        @Test
        void patchMultipleChoiceTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found.",
                                                org.springframework.http.HttpStatus.NOT_FOUND));

                mockMvc.perform(patch("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
                ;
        }

        @Test
        void patchMultipleChoiceTask_shouldReturnBadRequest_whenInvalidPatch() throws Exception {
                Long taskId = 999L;
                TaskPatchDTO patchDTO = new TaskPatchDTO("Patched", null, null, null, null);

                when(taskMapper.toPartialEntity(eq(taskId), any(TaskPatchDTO.class))).thenReturn(new Task());
                when(taskService.updateTask(any(Task.class)))
                                .thenThrow(new TaskException("Task " + taskId + " not found", HttpStatus.NOT_FOUND));

                mockMvc.perform(patch("/tasks/multiplechoice/{id}", taskId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patchDTO)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task " + taskId + " not found"))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
        }

        @Test
        void deleteTask_shouldReturnNoContent_whenTaskDeleted() throws Exception {
                Long taskId = 1L;
                mockMvc.perform(delete("/tasks/{id}", taskId))
                                .andExpect(status().isNoContent());
        }

        @Test
        void deleteTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                Long taskId = 999L;
                doThrow(new TaskException("Task with id " + taskId + " not found.",
                                org.springframework.http.HttpStatus.NOT_FOUND))
                                .when(taskService).deleteById(taskId);

                mockMvc.perform(delete("/tasks/{id}", taskId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task with id " + taskId + " not found."))
                                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.toString()));
        }

}

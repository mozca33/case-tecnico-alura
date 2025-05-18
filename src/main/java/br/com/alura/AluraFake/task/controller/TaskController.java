package br.com.alura.AluraFake.task.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.AluraFake.task.dto.BaseTaskDTO;
import br.com.alura.AluraFake.task.dto.MultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.OpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.service.TaskService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/task")
public class TaskController {

        private final TaskService taskService;
        private final TaskMapper taskMapper;

        public TaskController(TaskService taskService, TaskMapper taskMapper) {
                this.taskService = taskService;
                this.taskMapper = taskMapper;
        }

        @GetMapping("/{id}")
        public ResponseEntity<List<BaseTaskDTO>> getTasksFromCourse(@PathVariable Long id) {
                return ResponseEntity.ok().body(taskMapper.toDTO(taskService.findTasksByCourseId(id)));
        }

        @PostMapping("/new/opentext")
        public ResponseEntity<BaseTaskDTO> newOpenTextTask(@Valid @RequestBody OpenTextTaskDTO taskDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(taskMapper.toDTO(taskService
                                                .createTask(taskMapper.toEntity(taskDTO.withType(Type.OPEN_TEXT)))));
        }

        @PostMapping("/new/singlechoice")
        public ResponseEntity<BaseTaskDTO> newSingleChoiceTask(@Valid @RequestBody SingleChoiceTaskDTO taskDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(taskMapper.toDTO(
                                                taskService.createTask(taskMapper
                                                                .toEntity(taskDTO.withType(Type.SINGLE_CHOICE)))));
        }

        @PostMapping("/new/multiplechoice")
        public ResponseEntity<BaseTaskDTO> newMultipleChoice(@Valid @RequestBody MultipleChoiceTaskDTO taskDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(taskMapper.toDTO(
                                                taskService.createTask(
                                                                taskMapper.toEntity(taskDTO
                                                                                .withType(Type.MULTIPLE_CHOICE)))));
        }

        @PutMapping("/opentext/{id}")
        public ResponseEntity<BaseTaskDTO> updateOpenTextTask(@PathVariable Long id,
                        @Valid @RequestBody OpenTextTaskDTO taskDTO) {
                return ResponseEntity.ok(
                                taskMapper.toDTO(taskService.updateTask(
                                                taskMapper.toEntity(id, taskDTO.withType(Type.OPEN_TEXT)))));
        }

        @PutMapping("/singlechoice/{id}")
        public ResponseEntity<BaseTaskDTO> updateSingleChoiceTask(@PathVariable Long id,
                        @Valid @RequestBody SingleChoiceTaskDTO taskDTO) {
                return ResponseEntity.ok(taskMapper.toDTO(
                                taskService.updateTask(taskMapper.toEntity(id,
                                                taskDTO.withType(Type.SINGLE_CHOICE)))));
        }

        @PutMapping("/multiplechoice/{id}")
        public ResponseEntity<BaseTaskDTO> updateMultipleChoiceTask(@PathVariable Long id,
                        @Valid @RequestBody MultipleChoiceTaskDTO taskDTO) {
                return ResponseEntity.ok(taskMapper.toDTO(
                                taskService.updateTask(taskMapper.toEntity(id,
                                                taskDTO.withType(Type.MULTIPLE_CHOICE)))));
        }

        @PatchMapping("/opentext/{id}")
        public ResponseEntity<BaseTaskDTO> patchOpenTextTask(@PathVariable Long id,
                        @RequestBody TaskPatchDTO taskPatchDTO) {
                return ResponseEntity.ok(taskMapper.toDTO(taskService
                                .updateTask(taskMapper.toPartialEntity(id, taskPatchDTO.withType(Type.OPEN_TEXT)))));
        }

        @PatchMapping("/singlechoice/{id}")
        public ResponseEntity<BaseTaskDTO> patchSingleChoiceTask(@PathVariable Long id,
                        @RequestBody TaskPatchDTO taskPatchDTO) {
                return ResponseEntity
                                .ok(taskMapper.toDTO(taskService
                                                .updateTask(taskMapper.toPartialEntity(id,
                                                                taskPatchDTO.withType(Type.SINGLE_CHOICE)))));
        }

        @PatchMapping("/multiplechoice/{id}")
        public ResponseEntity<BaseTaskDTO> patchMultipleChoiceTask(@PathVariable Long id,
                        @Valid @RequestBody TaskPatchDTO taskDTO) {
                return ResponseEntity.ok(taskMapper.toDTO(
                                taskService.updateTask(taskMapper.toPartialEntity(id,
                                                taskDTO.withType(Type.MULTIPLE_CHOICE)))));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
                taskService.deleteById(id);

                return ResponseEntity.noContent().build();
        }

}

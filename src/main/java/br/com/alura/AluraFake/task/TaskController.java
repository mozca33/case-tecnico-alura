package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.alura.AluraFake.task.dto.TaskDTO;
import br.com.alura.AluraFake.task.dto.TaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.dto.TaskSingleChoiceDTO;
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.mapper.TaskMultipleChoiceMapper;
import br.com.alura.AluraFake.task.mapper.TaskPatchMapper;
import br.com.alura.AluraFake.task.mapper.TaskSingleChoiceMapper;

@RestController
@RequestMapping("/task")
public class TaskController {

        @Autowired
        private TaskService taskService;

        @PostMapping("/new/opentext")
        public ResponseEntity<TaskDTO> newOpenTextTask(@Valid @RequestBody TaskDTO taskDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(TaskMapper.toDTO(taskService
                                                .createTask(TaskMapper.toEntity(taskDTO.withType(Type.OPEN_TEXT)))));
        }

        @PostMapping("/new/singlechoice")
        public ResponseEntity<TaskSingleChoiceDTO> newSingleChoiceTask(
                        @Valid @RequestBody TaskSingleChoiceDTO taskDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(TaskSingleChoiceMapper.toDTO(
                                                taskService.createTask(TaskSingleChoiceMapper
                                                                .toEntity(taskDTO.withType(Type.SINGLE_CHOICE)))));
        }

        @PostMapping("/new/multiplechoice")
        public ResponseEntity<TaskMultipleChoiceDTO> newMultipleChoice(
                        @Valid @RequestBody TaskMultipleChoiceDTO taskDTO) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(TaskMultipleChoiceMapper.toDTO(
                                                taskService.createTask(
                                                                TaskMultipleChoiceMapper.toEntity(taskDTO
                                                                                .withType(Type.MULTIPLE_CHOICE)))));
        }

        @PutMapping("/opentext/{id}")
        public ResponseEntity<TaskDTO> updateOpenTextTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
                return ResponseEntity.ok(
                                TaskMapper.toDTO(taskService.updateTask(
                                                TaskMapper.toEntity(id, taskDTO.withType(Type.OPEN_TEXT)))));
        }

        @PutMapping("/singlechoice/{id}")
        public ResponseEntity<TaskSingleChoiceDTO> updateSingleChoiceTask(@PathVariable Long id,
                        @Valid @RequestBody TaskSingleChoiceDTO taskDTO) {
                return ResponseEntity.ok(TaskSingleChoiceMapper.toDTO(
                                taskService.updateTask(TaskSingleChoiceMapper.toEntity(id,
                                                taskDTO.withType(Type.SINGLE_CHOICE)))));
        }

        @PutMapping("/multiplechoice/{id}")
        public ResponseEntity<TaskMultipleChoiceDTO> updateMultipleChoiceTask(@PathVariable Long id,
                        @Valid @RequestBody TaskMultipleChoiceDTO taskDTO) {
                return ResponseEntity.ok(TaskMultipleChoiceMapper.toDTO(
                                taskService.updateTask(TaskMultipleChoiceMapper.toEntity(id,
                                                taskDTO.withType(Type.MULTIPLE_CHOICE)))));
        }

        @PatchMapping("/opentext/{id}")
        public ResponseEntity<TaskDTO> patchTask(@PathVariable Long id, @RequestBody TaskPatchDTO taskPatchDTO) {
                return ResponseEntity
                                .ok(TaskMapper.toDTO(taskService
                                                .updateTask(TaskPatchMapper.toPartialEntity(id,
                                                                taskPatchDTO.withType(Type.OPEN_TEXT)))));
        }

        @PatchMapping("/singlechoice/{id}")
        public ResponseEntity<TaskSingleChoiceDTO> patchSingleChoiceTask(@PathVariable Long id,
                        @RequestBody TaskPatchDTO taskPatchDTO) {
                return ResponseEntity
                                .ok(TaskSingleChoiceMapper.toDTO(taskService
                                                .updateTask(TaskPatchMapper.toPartialEntity(id,
                                                                taskPatchDTO.withType(Type.SINGLE_CHOICE)))));
        }

        @PatchMapping("/multiplechoice/{id}")
        public ResponseEntity<TaskMultipleChoiceDTO> patchMultipleChoiceTask(@PathVariable Long id,
                        @Valid @RequestBody TaskPatchDTO taskDTO) {
                return ResponseEntity.ok(TaskMultipleChoiceMapper.toDTO(
                                taskService.updateTask(TaskPatchMapper.toPartialEntity(id,
                                                taskDTO.withType(Type.MULTIPLE_CHOICE)))));
        }

}
package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.alura.AluraFake.task.dto.TaskDTO;
import br.com.alura.AluraFake.task.dto.TaskPatchDTO;
import br.com.alura.AluraFake.task.dto.TaskSingleChoiceDTO;
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.mapper.TaskPatchMapper;
import br.com.alura.AluraFake.task.mapper.TaskSingleChoiceMapper;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/new/opentext")
    public ResponseEntity<TaskDTO> newOpenTextTask(@Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskMapper.toDTO(taskService.createTask(TaskMapper.toEntity(taskDTO.withType(Type.OPEN_TEXT)))));
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<TaskSingleChoiceDTO> newSingleChoiceTask(@Valid @RequestBody TaskSingleChoiceDTO taskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskSingleChoiceMapper.toDTO(
                        taskService.createTask(TaskSingleChoiceMapper.toEntity(taskDTO.withType(Type.SINGLE_CHOICE)))));
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/opentext/{id}")
    public ResponseEntity<TaskDTO> updateOpenTextTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(TaskMapper.toDTO(taskService.updateTask(TaskMapper.toEntity(id, taskDTO.withType(Type.OPEN_TEXT)))));
    }

    @PutMapping("/singlechoice/{id}")
    public ResponseEntity<TaskSingleChoiceDTO> updateSingleChoiceTask(@PathVariable Long id, @Valid @RequestBody TaskSingleChoiceDTO taskDTO) {
        return ResponseEntity.ok(TaskSingleChoiceMapper.toDTO(taskService.updateTask(TaskSingleChoiceMapper.toEntity(id, taskDTO.withType(Type.SINGLE_CHOICE)))));
    }

    @PatchMapping("/opentext/{id}")
    public ResponseEntity<TaskDTO> patchTask(@PathVariable Long id, @RequestBody TaskPatchDTO taskPatchDTO) {
        return ResponseEntity
                .ok(TaskMapper.toDTO(taskService.patchTask(TaskPatchMapper.toPartialEntity(id, taskPatchDTO.withType(Type.OPEN_TEXT)))));
    }

    @PatchMapping("/singlechoice/{id}")
    public ResponseEntity<TaskSingleChoiceDTO> patchSingleChoiceTask(@PathVariable Long id, @RequestBody TaskPatchDTO taskPatchDTO) {
        return ResponseEntity
                .ok(TaskSingleChoiceMapper.toDTO(taskService.patchTask(TaskPatchMapper.toPartialEntity(id, taskPatchDTO.withType(Type.SINGLE_CHOICE)))));
    }

}
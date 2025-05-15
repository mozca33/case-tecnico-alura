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
import br.com.alura.AluraFake.task.mapper.TaskMapper;
import br.com.alura.AluraFake.task.mapper.TaskPatchMapper;

import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/new/opentext")
    public ResponseEntity<TaskDTO> newOpenTextTask(@Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TaskMapper
                        .toDTO(taskService.createTask(TaskMapper.toEntity(taskDTO.withType(Type.OPEN_TEXT)))));
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(TaskMapper.toDTO(taskService.updateTask(id, TaskMapper.toEntity(taskDTO))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> patchTask(@PathVariable Long id, @RequestBody TaskPatchDTO taskPatchDTO) {
        return ResponseEntity
                .ok(TaskMapper.toDTO(taskService.patchTask(id, TaskPatchMapper.toPartialEntity(taskPatchDTO))));
    }

}
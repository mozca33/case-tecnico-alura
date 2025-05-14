package br.com.alura.AluraFake.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/new/opentext")
    public ResponseEntity<TaskDTO> newOpenTextExercise(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO task = new TaskDTO(taskDTO.id(), taskDTO.courseId(), taskDTO.statement(), taskDTO.order(),
                Type.OPEN_TEXT);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

}
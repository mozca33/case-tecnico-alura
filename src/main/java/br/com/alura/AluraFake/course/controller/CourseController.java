package br.com.alura.AluraFake.course.controller;

import br.com.alura.AluraFake.course.dto.CourseDTO;
import br.com.alura.AluraFake.course.dto.CoursePatchDTO;
import br.com.alura.AluraFake.course.mapper.CourseMapper;
import br.com.alura.AluraFake.course.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

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

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @PostMapping()
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseMapper.toDTO(
                        courseService.createCourse(courseMapper.toEntity(courseDTO))));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseDTO> publishCourse(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok().body(courseMapper.toDTO(courseService.publishCourse(id)));
    }

    @GetMapping()
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok().body(courseMapper.toDTO(courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok().body(courseMapper.toDTO(courseService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable @Min(1) Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok().body(
                courseMapper.toDTO(courseService.updateCourse(id, courseMapper.toEntity(courseDTO))));
    }

    }

}

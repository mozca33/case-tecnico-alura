package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseDTO;
import br.com.alura.AluraFake.course.mapper.CourseMapper;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @Transactional
    @PostMapping("/new")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseMapper.toDTO(
                        courseService.createCourse(courseMapper.toEntity(courseDTO))));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseListItemDTO>> getAllCourses() {
        return ResponseEntity.ok().body(courseMapper.toDTO(courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok().body(courseMapper.toDTO(courseService.getById(id)));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseDTO> publishCourse(@PathVariable Long id) {
        return ResponseEntity.ok().body(courseMapper.toDTO(courseService.publishCourse(id)));
    }

    @PutMapping("/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        // TODO: process PUT request

        return entity;
    }

}

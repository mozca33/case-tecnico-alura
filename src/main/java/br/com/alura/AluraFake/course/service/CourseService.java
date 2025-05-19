package br.com.alura.AluraFake.course.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.course.enums.Status;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.course.validator.CourseValidator;
import br.com.alura.AluraFake.task.exceptions.TaskException;
import br.com.alura.AluraFake.user.validator.UserValidator;
import jakarta.transaction.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseValidator courseValidator;
    private final UserValidator userValidator;

    public CourseService(CourseRepository courseRepository, CourseValidator courseValidator,
            UserValidator userValidator) {
        this.courseRepository = courseRepository;
        this.courseValidator = courseValidator;
        this.userValidator = userValidator;
    }

    @Transactional
    public Course createCourse(Course course) {
        if (courseRepository.existsByTitle(course.getTitle())) {
            throw new CourseException("Course " + course.getTitle() + " already exists.", HttpStatus.CONFLICT);
        }

        userValidator.validateUserIsInstructor(course.getInstructor());

        return courseRepository.save(course);
    };

    public Course getById(Long id) {
        if (id <= 0) {
            throw new TaskException("Course id must be a positive value.", HttpStatus.BAD_REQUEST);
        }
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseException("Course with id " + id + " not found.", HttpStatus.NOT_FOUND));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course publishCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseException("Course with id " + id + " not found.", HttpStatus.NOT_FOUND));

        courseValidator.validateForPublishing(course);

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        return courseRepository.save(course);

    }

    public Course updateCourse(Long id, Course course) {
        Course existingCourse = findByCourseId(id); // validar se é o mesmo antes de ir para o banco
        // adicionar throw se der tempo

        courseValidator.validateCourseIsInBuildingStatus(existingCourse.getStatus());

        mergeCourse(existingCourse, course);
        return courseRepository.save(existingCourse);
    }
    public void validateCourseIsInBuildingStatus(Status status) {
        courseValidator.validateCourseIsInBuildingStatus(status);
    }

    private void mergeCourse(Course existingCourse, Course course) {
        if (course.getInstructor() != null && !course.getInstructor().equals(existingCourse.getInstructor())) {
            userService.validateUserIsInstructor(course.getInstructor());
            existingCourse.setInstructor(course.getInstructor());
        }

        if (course.getTitle() != null && !course.getTitle().equals(existingCourse.getTitle())) {
            if (courseRepository.existsByTitle(course.getTitle())) {
                throw new CourseException("Course " + course.getTitle() + " already exists.", HttpStatus.CONFLICT);
            }

            existingCourse.setTitle(course.getTitle());
        }

        if (course.getDescription() != null) {
            existingCourse.setDescription(course.getDescription());
        }
    }

    private Course findByCourseId(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseException("Course with id " + id + " not found.", HttpStatus.NOT_FOUND));
    }
}

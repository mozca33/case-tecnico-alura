package br.com.alura.AluraFake.course;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.task.exceptions.TaskException;

@Component
public class CourseValidator {

    private final CourseRepository courseRepository;

    public CourseValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void validateCourseIsInBuildingStatus(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new TaskException("Course " + courseId + " not found.", HttpStatus.NOT_FOUND));

        if (course.getStatus() != Status.BUILDING) {
            throw new TaskException("Course " + courseId + " is not in BUILDING status.",
                    HttpStatus.CONFLICT);
        }
    }
}

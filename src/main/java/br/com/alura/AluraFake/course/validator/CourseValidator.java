package br.com.alura.AluraFake.course.validator;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.models.Task;

@Component
public class CourseValidator {

    private final CourseRepository courseRepository;

    public CourseValidator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void validateCourseIsInBuildingStatus(Status status) {
        if (status != Status.BUILDING) {
            throw new CourseException("Course is not in BUILDING status.",
                    HttpStatus.CONFLICT);
        }
    }

    public void validateCourseExistsById(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseException("Course with id " + courseId + " not found.", HttpStatus.NOT_FOUND);
        }
    }

    public void validateForPublishing(Course course) {
        validateCourseExistsById(course.getId());
        validateCourseIsInBuildingStatus(course.getStatus());
        validateCourseHasAllTypesOfTasks(course.getTasks());
    }

    private void validateCourseHasAllTypesOfTasks(List<Task> tasks) {
        if (!hasAllRequiredTaskTypes(tasks)) {
            throw new CourseException("The course does not have all types of task.", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean hasAllRequiredTaskTypes(List<Task> tasks) {
        boolean hasOpenText = false;
        boolean hasSingleChoice = false;
        boolean hasMultipleChoice = false;

        for (Task task : tasks) {
            if (task.isOpenText())
                hasOpenText = true;
            if (task.isSingleChoice())
                hasSingleChoice = true;
            if (task.isMultipleChoice())
                hasMultipleChoice = true;
        }

        return hasOpenText && hasSingleChoice && hasMultipleChoice;
    }
}

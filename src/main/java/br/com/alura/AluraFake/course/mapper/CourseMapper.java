package br.com.alura.AluraFake.course.mapper;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.dto.CourseDTO;
import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.service.UserService;

@Component
public class CourseMapper {
    private final UserService userService;

    public CourseMapper(UserService userService) {
        this.userService = userService;
    }

    public static Course toEntity(Long courseId, CourseRepository courseRepository) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseException("Course " + courseId + " not found", HttpStatus.NOT_FOUND));
    }

    public Course toEntity(CourseDTO dto) {
        User instructor = userService.findByEmail(dto.emailInstructor())
                .orElseThrow(() -> new CourseException("Instructor with email " + dto.emailInstructor() + " not found",
                        HttpStatus.BAD_REQUEST));

        if (!instructor.isInstructor()) {
            throw new CourseException("User is not an instructor.", HttpStatus.FORBIDDEN);
        }

        return new Course(dto.title(), dto.description(), instructor);
    }

    public CourseDTO toDTO(Course course) {
        return new CourseDTO(course.getId(), course.getTitle(), course.getDescription(),
                course.getInstructor().getEmail(), course.getStatus());
    }

    public List<CourseDTO> toDTO(List<Course> courses) {
        return courses.stream()
                .map(course -> new CourseDTO(course.getId(), course.getTitle(), course.getDescription(),
                        course.getInstructor().getEmail(), course.getStatus()))
                .toList();
    }

}

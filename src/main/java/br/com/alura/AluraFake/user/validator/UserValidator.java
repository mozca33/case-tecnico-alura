package br.com.alura.AluraFake.user.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.user.models.User;

@Component
public class UserValidator {
    public void validateUserIsInstructor(User instructor) {
        if (!instructor.isInstructor()) {
            throw new CourseException("Informed user is not a instructor", HttpStatus.BAD_REQUEST);
        }
    }

}

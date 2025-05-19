package br.com.alura.AluraFake.user.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.user.exceptions.UserException;
import br.com.alura.AluraFake.user.models.User;

@Component
public class UserValidator {
    public void validateUserIsInstructor(User instructor) {
        if (!instructor.isInstructor()) {
            throw new CourseException("Informed user is not a instructor", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateEmailFormat(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new UserException(email, HttpStatus.BAD_REQUEST);
        }
    }

    public void validateUserForUpdate(User user) {
        if (user == null) {
            return;
        }

        validateEmailFormat(user.getEmail());

        if (user.getEmail() != null && (user.getEmail().isBlank())) {
            throw new UserException("Invalid email format", HttpStatus.BAD_REQUEST);
        }
        if (user.getName() != null && user.getName().trim().isEmpty()) {
            throw new UserException("Name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (user.getRole() == null) {
            throw new UserException("Role cannot be null", HttpStatus.BAD_REQUEST);
        }
    }

}

package br.com.alura.AluraFake.user.validator;

import br.com.alura.AluraFake.course.exceptions.CourseException;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.exceptions.UserException;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidatorTest {

    private UserValidator userValidator;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userValidator = new UserValidator(userRepository);
    }

    @Test
    void validateUserIsInstructor_shouldNotThrow_whenUserIsInstructor() {
        User instructor = mock(User.class);
        when(instructor.isInstructor()).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.validateUserIsInstructor(instructor));
    }

    @Test
    void validateUserIsInstructor_shouldThrow_whenUserIsNotInstructor() {
        User notInstructor = mock(User.class);
        when(notInstructor.isInstructor()).thenReturn(false);

        CourseException ex = assertThrows(CourseException.class,
                () -> userValidator.validateUserIsInstructor(notInstructor));
        assertEquals("Informed user is not a instructor", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateEmailFormat_shouldNotThrow_whenEmailIsValid() {
        assertDoesNotThrow(() -> userValidator.validateEmailFormat("test@example.com"));
        assertDoesNotThrow(() -> userValidator.validateEmailFormat("user.name+tag@domain.co"));
    }

    @Test
    void validateEmailFormat_shouldThrow_whenEmailIsInvalid() {
        UserException ex = assertThrows(UserException.class, () -> userValidator.validateEmailFormat("invalid-email"));
        assertEquals("Email is not in a valid format", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateUserForUpdate_shouldNotThrow_whenUserIsValid() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("valid@email.com");
        when(user.getName()).thenReturn("Valid Name");
        when(user.getRole()).thenReturn(Role.INSTRUCTOR);

        assertDoesNotThrow(() -> userValidator.validateUserForUpdate(user));
    }

    @Test
    void validateUserForUpdate_shouldThrow_whenEmailIsEmptyString() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("");
        when(user.getName()).thenReturn("Valid Name");
        when(user.getRole()).thenReturn(Role.INSTRUCTOR);

        UserException ex = assertThrows(UserException.class, () -> userValidator.validateUserForUpdate(user));
        assertEquals("Email is not in a valid format", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateUserForUpdate_shouldThrow_whenNameIsEmptyString() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("valid@email.com");
        when(user.getName()).thenReturn("");
        when(user.getRole()).thenReturn(Role.INSTRUCTOR);

        UserException ex = assertThrows(UserException.class, () -> userValidator.validateUserForUpdate(user));
        assertEquals("Name cannot be empty", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateEmailFormat_shouldThrow_whenEmailIsNull() {
        assertThrows(NullPointerException.class, () -> userValidator.validateEmailFormat(null));
    }

    @Test
    void validateUserForUpdate_shouldThrow_whenEmailAlreadyExists() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("existing@email.com");
        when(user.getName()).thenReturn("Valid Name");
        when(user.getRole()).thenReturn(Role.INSTRUCTOR);
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        UserException ex = assertThrows(UserException.class, () -> userValidator.validateUserForUpdate(user));
        assertEquals("User with informed email already exists", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void validateUserForUpdate_shouldNotThrow_whenAllFieldsAreValidAndEmailDoesNotExist() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("unique@email.com");
        when(user.getName()).thenReturn("Unique Name");
        when(user.getRole()).thenReturn(Role.STUDENT);
        when(userRepository.existsByEmail("unique@email.com")).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUserForUpdate(user));
    }

    @Test
    void validateUserForUpdate_shouldNotThrow_whenUserIsNull() {
        assertDoesNotThrow(() -> userValidator.validateUserForUpdate(null));
    }

}
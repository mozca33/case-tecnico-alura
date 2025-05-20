package br.com.alura.AluraFake.user.service;

import br.com.alura.AluraFake.user.exceptions.UserException;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.user.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserValidator userValidator;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userValidator = mock(UserValidator.class);
        userService = new UserService(userRepository, userValidator);
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        User user = new User();
        user.setEmail("test@email.com");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@email.com");

        assertTrue(result.isPresent());
        assertEquals("test@email.com", result.get().getEmail());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenUserDoesNotExist() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("notfound@email.com");

        assertFalse(result.isPresent());
    }

    @Test
    void createUser_shouldSaveAndReturnUser_whenEmailDoesNotExist() {
        User user = new User();
        user.setEmail("unique@email.com");
        when(userRepository.existsByEmail("unique@email.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertEquals(user, created);
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        User user = new User();
        user.setEmail("duplicate@email.com");
        when(userRepository.existsByEmail("duplicate@email.com")).thenReturn(true);

        UserException ex = assertThrows(UserException.class, () -> userService.createUser(user));
        assertEquals("User already exists with this email", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserException ex = assertThrows(UserException.class, () -> userService.findById(99L));
        assertEquals("User not found", ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser_whenUserExists() {
        User existing = mock(User.class);
        User updated = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateUser(1L, updated);

        verify(userValidator).validateUserForUpdate(updated);
        verify(existing).mergeFrom(updated);
        verify(userRepository).save(existing);
        assertEquals(existing, result);
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        User user = new User();
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deleteUser(2L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.deleteUser(3L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void validateUserIsInstructor_shouldCallValidator() {
        User user = new User();
        userService.validateUserIsInstructor(user);
        verify(userValidator).validateUserIsInstructor(user);
    }

    @Test
    void validateUser_shouldCallValidator() {
        User user = new User();
        userService.validateUser(user);
        verify(userValidator).validateUserForUpdate(user);
    }
}
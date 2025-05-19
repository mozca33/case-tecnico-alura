package br.com.alura.AluraFake.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.user.exceptions.UserException;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.repository.UserRepository;
import br.com.alura.AluraFake.user.validator.UserValidator;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public Optional<User> findByEmail(String emailInstructor) {
        return userRepository.findByEmail(emailInstructor);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("User already exists with this email", HttpStatus.CONFLICT);
        }

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = findById(id);

        userValidator.validateUserForUpdate(updatedUser);

        existingUser.mergeFrom(updatedUser);

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public void validateUserIsInstructor(User user) {
        userValidator.validateUserIsInstructor(user);
    }

    public void validateUser(User user) {
        userValidator.validateUserForUpdate(user);
    }

}

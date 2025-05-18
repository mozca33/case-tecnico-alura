package br.com.alura.AluraFake.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String emailInstructor) {
        return userRepository.findByEmail(emailInstructor);
    }

}

package br.com.alura.AluraFake.infra.data;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.alura.AluraFake.course.model.Course;
import br.com.alura.AluraFake.course.repository.CourseRepository;
import br.com.alura.AluraFake.user.enums.Role;
import br.com.alura.AluraFake.user.models.User;
import br.com.alura.AluraFake.user.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile))
            return;

        if (userRepository.count() == 0) {
            User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT);
            User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
            userRepository.saveAll(Arrays.asList(caio, paulo));
            courseRepository.save(new Course("Java", "Aprenda Java com Alura", paulo));
        }
    }
}
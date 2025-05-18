package br.com.alura.AluraFake.course;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.AluraFake.course.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByTitle(String title);

}

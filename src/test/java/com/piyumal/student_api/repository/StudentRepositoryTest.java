package com.piyumal.student_api.repository;

import com.piyumal.student_api.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Should save and retrieve a student by ID")
    void shouldSaveAndFindStudentById() {
        Student student = new Student("Alice Johnson", "alice@example.com", "Computer Science");
        Student saved = studentRepository.save(student);

        Optional<Student> found = studentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice Johnson");
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("Should verify email existence")
    void shouldCheckIfEmailExists() {
        Student student = new Student("Bob Smith", "bob@example.com", "Mathematics");
        studentRepository.save(student);

        boolean exists = studentRepository.existsByEmail("bob@example.com");
        boolean notExists = studentRepository.existsByEmail("other@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should check if email exists for another student ID")
    void shouldCheckEmailExcludingGivenId() {
        Student s1 = studentRepository.save(new Student("Charlie", "charlie@example.com", "Physics"));
        Student s2 = studentRepository.save(new Student("David", "david@example.com", "Chemistry"));

        boolean takenByAnother = studentRepository.existsByEmailAndIdNot("david@example.com", s1.getId());
        boolean sameStudent = studentRepository.existsByEmailAndIdNot("charlie@example.com", s1.getId());

        assertThat(takenByAnother).isTrue();
        assertThat(sameStudent).isFalse();
    }
}

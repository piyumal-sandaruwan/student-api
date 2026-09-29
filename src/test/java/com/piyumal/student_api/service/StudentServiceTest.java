package com.piyumal.student_api.service;

import com.piyumal.student_api.dto.StudentRequest;
import com.piyumal.student_api.dto.StudentResponse;
import com.piyumal.student_api.entity.Student;
import com.piyumal.student_api.exception.BadRequestException;
import com.piyumal.student_api.exception.ResourceNotFoundException;
import com.piyumal.student_api.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private StudentRequest studentRequest;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "John Doe", "john@example.com", "Computer Science");
        studentRequest = new StudentRequest("John Doe", "john@example.com", "Computer Science");
    }

    @Test
    @DisplayName("Should return all students")
    void shouldReturnAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<StudentResponse> result = studentService.getAllStudents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return student by ID when exists")
    void shouldReturnStudentByIdWhenExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponse response = studentService.getStudentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student does not exist")
    void shouldThrowExceptionWhenStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student not found with id: 99");
    }

    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudentSuccessfully() {
        when(studentRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.createStudent(studentRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if email is already taken")
    void shouldThrowExceptionWhenEmailExistsOnCreate() {
        when(studentRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(studentRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already registered");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update student successfully")
    void shouldUpdateStudentSuccessfully() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmailAndIdNot("john@example.com", 1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.updateStudent(1L, studentRequest);

        assertThat(response).isNotNull();
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should delete student when exists")
    void shouldDeleteStudentWhenExists() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent student")
    void shouldThrowExceptionWhenDeletingNonExistentStudent() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, never()).deleteById(anyLong());
    }
}

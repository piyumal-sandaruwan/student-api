package com.piyumal.student_api.controller;

import com.piyumal.student_api.entity.Student;
import com.piyumal.student_api.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanUp() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/students - should return empty list initially")
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/students - should create new student")
    void shouldCreateStudent() throws Exception {
        String requestJson = """
                {
                    "name": "Jane Doe",
                    "email": "jane@example.com",
                    "course": "Software Engineering"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("jane@example.com")))
                .andExpect(jsonPath("$.course", is("Software Engineering")));
    }

    @Test
    @DisplayName("POST /api/students - should fail with 400 when validation fails")
    void shouldFailValidationWhenCreatingStudent() throws Exception {
        String invalidRequestJson = """
                {
                    "name": "",
                    "email": "invalid-email",
                    "course": ""
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.email", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.course", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/students/{id} - should return student by ID")
    void shouldReturnStudentById() throws Exception {
        Student saved = studentRepository.save(new Student("Mark Taylor", "mark@example.com", "Data Science"));

        mockMvc.perform(get("/api/students/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Mark Taylor")))
                .andExpect(jsonPath("$.email", is("mark@example.com")));
    }

    @Test
    @DisplayName("GET /api/students/{id} - should return 404 when student not found")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Student not found with id: 999")));
    }

    @Test
    @DisplayName("PUT /api/students/{id} - should update student")
    void shouldUpdateStudent() throws Exception {
        Student saved = studentRepository.save(new Student("Old Name", "old@example.com", "Math"));
        String updateRequestJson = """
                {
                    "name": "New Name",
                    "email": "new@example.com",
                    "course": "Physics"
                }
                """;

        mockMvc.perform(put("/api/students/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andExpect(jsonPath("$.course", is("Physics")));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - should delete student")
    void shouldDeleteStudent() throws Exception {
        Student saved = studentRepository.save(new Student("To Delete", "delete@example.com", "Arts"));

        mockMvc.perform(delete("/api/students/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}

package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private AvatarService avatarService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getStudentInfo_shouldReturnStudent_whenStudentExists() throws Exception {
        Student student = new Student(1L, "Егор", 20);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Егор"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void createStudent_shouldCreateAndReturnStudent() throws Exception {
        Student newStudent = new Student(null, "Алексей", 25);
        Student savedStudent = new Student(3L, "Алексей", 25);

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Алексей"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void deleteStudent_shouldDeleteStudent() throws Exception {
        when(studentRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void findStudent_shouldReturnStudents_whenAgeGreaterThan18() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Егор", 20),
                new Student(2L, "Мария", 25));

        when(studentService.findByAge(20)).thenReturn(students);

        mockMvc.perform(get("/student/age").param("age", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].age").value(20))
                .andExpect(jsonPath("$[1].age").value(25));
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "ПК", "Красный");

        when(studentService.getStudentFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ПК"))
                .andExpect(jsonPath("$.color").value("Красный"));
    }
}
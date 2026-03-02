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

    private Student createStudent(Long id, String name, int age) {
        Student student = new Student(id, name, age);
        return student;
    }

    private final Student student = createStudent(1L, "Егор", 20);

    @Test
    void getStudentInfo_shouldReturnStudent_whenStudentExists() throws Exception {

        when(studentService.findStudent(student.getId())).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
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
                .andExpect(jsonPath("$.id").value(savedStudent.getId()))
                .andExpect(jsonPath("$.name").value(savedStudent.getName()))
                .andExpect(jsonPath("$.age").value(savedStudent.getAge()));
    }

    @Test
    void deleteStudent_shouldDeleteStudent() throws Exception {
        when(studentRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/student/{id}", student.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findStudent_shouldReturnStudents_whenAgeGreaterThan18() throws Exception {
        int searchAge = 20;
        List<Student> students = Arrays.asList(
                new Student(1L, "Егор", searchAge),
                new Student(2L, "Мария", searchAge + 1));

        when(studentService.findByAge(searchAge)).thenReturn(students);

        mockMvc.perform(get("/student/age").param("age", String.valueOf(searchAge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(students.size()))
                .andExpect(jsonPath("$[0].age").value(students.get(0).getAge()))
                .andExpect(jsonPath("$[1].age").value(students.get(1).getAge()));
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {
        Long studentId = 1L;
        Faculty faculty = new Faculty(1L, "ПК", "Красный");

        when(studentService.getStudentFaculty(studentId)).thenReturn(faculty);

        mockMvc.perform(get("/student/{studentId}/faculty", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }
}
package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private Student createTestStudent() {
        Student student = new Student();
        student.setName("Егор");
        student.setAge(20);
        return studentRepository.save(student);
    }

    @Test
    void getStudentInfo_shouldReturnStudent_whenStudentExists() {
        Student savedStudent = createTestStudent();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student" + "/" + savedStudent.getId(),
                Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedStudent.getId());
        assertThat(response.getBody().getName()).isEqualTo("Егор");
    }

    @Test
    void createStudent_shouldCreateAndReturnStudent() {
        Student newStudent = new Student();
        newStudent.setName("Антон");
        newStudent.setAge(20);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student", newStudent, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Антон");
    }

    @Test
    void deleteStudent_shouldDeleteStudent() {
        Student savedStudent = createTestStudent();
        Long studentId = savedStudent.getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/student" + "/" + studentId,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRepository.existsById(studentId)).isFalse();
    }

    @Test
    void findStudent_shouldReturnStudentsByAge_whenAgeProvidedAndOver18() {

        Student student1 = createTestStudent();
        Student student2 = new Student();
        student2.setName("Антон");
        student2.setAge(20);
        student2.setFaculty(student1.getFaculty());
        studentRepository.save(student2);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "http://localhost:" + port + "/student" + "/age?age=20",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0).getAge()).isEqualTo(20);

    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() {

        Faculty faculty = new Faculty();
        faculty.setName("ПК");
        faculty.setColor("Красный");
        Faculty savedFaculty = facultyRepository.save(faculty);

        Student student = new Student();
        student.setName("Андрей");
        student.setAge(20);
        student.setFaculty(savedFaculty);
        Student savedStudent = studentRepository.save(student);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student" + "/" + savedStudent.getId() + "/faculty",
                Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("ПК");
        assertThat(response.getBody().getColor()).isEqualTo("Красный");
    }
}
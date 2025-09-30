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
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private Faculty createTestFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("ПК");
        faculty.setColor("Красный");
        return facultyRepository.save(faculty);
    }

    @Test
    void getStudentInfo_shouldReturnStudent_whenStudentExists() {
        Faculty savedFaculty = createTestFaculty();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty" + "/" + savedFaculty.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedFaculty.getId());
        assertThat(response.getBody().getName()).isEqualTo("ПК");
        assertThat(response.getBody().getColor()).isEqualTo("Красный");
    }

    @Test
    void createFaculty_shouldCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Математика");
        faculty.setColor("Синий");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty", faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Математика");
        assertThat(response.getBody().getColor()).isEqualTo("Синий");
    }

    @Test
    void deleteFaculty_shouldDeleteFaculty() {

        Faculty savedFaculty = createTestFaculty();
        Long facultyId = savedFaculty.getId();


        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty" + "/" + facultyId,
                HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.existsById(facultyId)).isFalse();
    }

    @Test
    void searchFaculties_shouldReturnFaculties_whenSearchTermProvided() {
        Faculty faculty1 = new Faculty();
        faculty1.setName("ПК");
        faculty1.setColor("Красный");
        facultyRepository.save(faculty1);

        Faculty faculty2 = new Faculty();
        faculty2.setName("Математика");
        faculty2.setColor("Синий");
        facultyRepository.save(faculty2);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty" + "/faculties?search=Математика",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Faculty>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .extracting(Faculty::getName)
                .allMatch(name -> name.contains("Математика"));
    }
}
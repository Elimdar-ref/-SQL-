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
import org.springframework.test.web.servlet.RequestBuilder;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FacultyService facultyService;

    @MockBean
    private AvatarService avatarService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getFacultyInfo_shouldReturnFaculty_whenFacultyExists() throws Exception {
        Faculty faculty = new Faculty(1L, "ПК", "Красный");

        when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ПК"))
                .andExpect(jsonPath("$.color").value("Красный"));

    }

    @Test
    void createFaculty_shouldCreateAndReturnFaculty() throws Exception {
        Faculty newFaculty = new Faculty(null, "Новый факультет", "Синий");
        Faculty savedFaculty = new Faculty(2L, "Новый факультет", "Синий");

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Новый факультет"))
                .andExpect(jsonPath("$.color").value("Синий"));
    }

        @Test
        void deleteFaculty_shouldDeleteFaculty() throws Exception {
            mockMvc.perform(delete("/faculty/1"))
                    .andExpect(status().isOk());
    }

    @Test
    void findFaculty_shouldReturnFaculties_whenColorProvided() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "ПК", "Красный"),
                new Faculty(2L, "ИВТ", "Красный")
        );
        when(facultyService.findByColor("Красный")).thenReturn(faculties);

        mockMvc.perform(get("/faculty").param("color", "Красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].color").value("Красный"))
                .andExpect(jsonPath("$[1].color").value("Красный"));
    }

    @Test
    void searchFaculties_shouldReturnFaculties_whenSearchTermProvided() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "ПК", "Красный"),
                new Faculty(3L, "ПК-2", "Синий"));

        when(facultyService.searchFaculties("ПК")).thenReturn(faculties);

        mockMvc.perform(get("/faculty/faculties").param("search", "ПК"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("ПК"))
                .andExpect(jsonPath("$[1].name").value("ПК-2"));
    }
}
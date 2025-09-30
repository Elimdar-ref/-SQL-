package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

    private Faculty createFaculty(Long id, String name, String color) {
        Faculty faculty = new Faculty(id, name, color);
        return faculty;
    }

    private final Faculty faculty = createFaculty(1L, "ПК", "Красный");

    @Test
    void getFacultyInfo_shouldReturnFaculty_whenFacultyExists() throws Exception {

        when(facultyService.findFaculty(faculty.getId())).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
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
                .andExpect(jsonPath("$.id").value(savedFaculty.getId()))
                .andExpect(jsonPath("$.name").value(savedFaculty.getName()))
                .andExpect(jsonPath("$.color").value(savedFaculty.getColor()));
    }

        @Test
        void deleteFaculty_shouldDeleteFaculty() throws Exception {
            mockMvc.perform(delete("/faculty/{id}", faculty.getId()))
                    .andExpect(status().isOk());
    }

    @Test
    void findFaculty_shouldReturnFaculties_whenColorProvided() throws Exception {
        String searchColor = "Красный";
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "ПК", searchColor),
                new Faculty(2L, "ИВТ", searchColor)
        );
        when(facultyService.findByColor(searchColor)).thenReturn(faculties);

        mockMvc.perform(get("/faculty").param("color", searchColor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(faculties.size()))
                .andExpect(jsonPath("$[0].color").value(searchColor))
                .andExpect(jsonPath("$[1].color").value(searchColor));
    }

    @Test
    void searchFaculties_shouldReturnFaculties_whenSearchTermProvided() throws Exception {
        String searchTerm = "ПК";
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "ПК", "Красный"),
                new Faculty(3L, "ПК-2", "Синий"));

        when(facultyService.searchFaculties(searchTerm)).thenReturn(faculties);

        mockMvc.perform(get("/faculty/faculties").param("search", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(faculties.size()))
                .andExpect(jsonPath("$[0].name").value(faculties.get(0).getName()))
                .andExpect(jsonPath("$[1].name").value(faculties.get(1).getName()));
    }
}
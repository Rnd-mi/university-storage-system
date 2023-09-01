package ru.hogwarts.school;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.hogwarts.school.constants.Constants.*;

@WebMvcTest(FacultyController.class)
public class WebMvcTestFaculty {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private FacultyServiceImpl facultyService;

    @InjectMocks
    private FacultyController facultyController;

    private final static String RESOURCE = "/faculties";

    @Test
    public void testCreateFaculty() throws Exception {
        JSONObject facultyObject = getTestFacultyObject();
        Faculty faculty = getTestFaculty(TEST);

        whenFacultyIsAccessed(faculty);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE)
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void createFaculty_shouldThrowIfGivenInvalidInput() throws Exception {
        JSONObject facultyObject = getTestFacultyObject();
        facultyObject.put("name", BLANK_STR);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE)
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createFaculty_shouldThrowIfFacultyAlreadyExist() throws Exception {
        JSONObject studentObject = getTestFacultyObject();
        Faculty faculty = getTestFaculty(TEST);

        whenFacultyIsAccessed(faculty);
        doThrow(new RuntimeException()).when(facultyRepository).save(any(Faculty.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetFaculty() throws Exception {
        Faculty faculty = getTestFaculty(TEST);

        whenFacultyIsAccessed(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/" + faculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void getFaculty_shouldSendNotFoundMessage() throws Exception {
        JSONObject facultyObject = getTestFacultyObject();
        Faculty faculty = getTestFaculty(TEST);

        when(studentRepository.existsById(eq(faculty.getId()))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/" + faculty.getId())
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testUpdateFaculty() throws Exception {
        JSONObject facultyObject = getTestFacultyObject();
        Faculty faculty = getTestFaculty(TEST);

        whenFacultyIsAccessed(faculty);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders
                        .put(RESOURCE)
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()));
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Faculty faculty = getTestFaculty(TEST);

        whenFacultyIsAccessed(FACULTY);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(RESOURCE + "/" + FACULTY_ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    public void getFacultiesOfColor_shouldThrowIfThereAreZeroFacultiesOfThatColor() throws Exception {
        when(studentRepository.findByAge(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/search?color=" + COLOR)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFacultiesByColorOrName_shouldThrowIfResultIsEmpty() throws Exception {
        when(facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(TEST, COLOR)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/multi-search?color=" + COLOR + "&name=" + TEST)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFacultiesByColorOrName_shouldThrowIfGivenInvalidInput() throws Exception {
        when(facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(anyString(), anyString()))
                .thenReturn(List.of(getTestFaculty(TEST), getTestFaculty(TEST2)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/multi-search?color=" + COLOR + "&name=" + TEST)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void testGetFacultyStudents() throws Exception {
        Faculty faculty = getTestFaculty(TEST);

        whenFacultyIsAccessed(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/" + FACULTY_ID + "/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        List<Faculty> faculties = List.of(getTestFaculty(TEST), getTestFaculty(TEST2));

        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(faculties.get(0)))
                .andExpect(jsonPath("$.[1]").value(faculties.get(1)));
    }

    @Test
    public void getAllFaculties_shouldThrowIfThereIsNoFaculties() throws Exception {
        when(facultyRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private JSONObject getTestFacultyObject() throws JSONException {
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", FACULTY_ID);
        facultyObject.put("name", TEST);
        facultyObject.put("color", COLOR);
        return facultyObject;
    }

    private Faculty getTestFaculty(String name) {
        FACULTY.setName(name);
        FACULTY.setStudents(getStudents());
        return FACULTY;
    }

    private Student getTestStudent(long id, String name) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(AGE);
        student.setFaculty(FACULTY);
        return student;
    }

    private Set<Student> getStudents() {
        return Set.of(getTestStudent(ID, TEST), getTestStudent(ID2, TEST2));
    }

    private void whenFacultyIsAccessed(Faculty faculty) {
        when(facultyRepository.existsById(eq(faculty.getId()))).thenReturn(true);
        when(facultyRepository.findById(eq(faculty.getId()))).thenReturn(Optional.of(FACULTY));
    }
}

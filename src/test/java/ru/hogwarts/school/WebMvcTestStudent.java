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
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyServiceImpl;
import ru.hogwarts.school.service.StudentServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.hogwarts.school.constants.Constants.*;

@WebMvcTest(StudentController.class)
public class WebMvcTestStudent {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private AvatarRepository avatarRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private StudentServiceImpl studentService;

    @SpyBean
    private FacultyServiceImpl facultyService;

    @InjectMocks
    private StudentController studentController;

    private final static String RESOURCE = "/students";

    @Test
    public void testCreateStudent() throws Exception {
        JSONObject studentObject = getTestStudentObject();

        Student student = getTestStudent(TEST);

        whenFacultyIsAccessed(FACULTY);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE + "/" + FACULTY_ID)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(TEST))
                .andExpect(jsonPath("$.age").value(AGE));
    }

    @Test
    public void createStudent_shouldThrowIfFacultyNotPresent() throws Exception {
        JSONObject studentObject = getTestStudentObject();

        when(facultyRepository.existsById(eq(FACULTY_ID))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE + "/" + FACULTY_ID)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createStudent_shouldThrowIfGivenInvalidInput() throws Exception {
        JSONObject studentObject = getTestStudentObject();
        studentObject.put("name", BLANK_STR);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE + "/" + FACULTY_ID)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createStudent_shouldThrowIfStudentAlreadyExist() throws Exception {
        JSONObject studentObject = getTestStudentObject();

        whenFacultyIsAccessed(FACULTY);
        doThrow(new RuntimeException()).when(studentRepository).save(any(Student.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post(RESOURCE + "/" + FACULTY_ID)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetStudent() throws Exception {
        Student student = getTestStudent(TEST);

        whenStudentIsAccessed(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(TEST))
                .andExpect(jsonPath("$.age").value(AGE));
    }

    @Test
    public void getStudent_shouldSendNotFoundMessage() throws Exception {
        JSONObject studentObject = getTestStudentObject();
        Student student = getTestStudent(TEST);

        when(studentRepository.existsById(eq(student.getId()))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/" + student.getId())
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testUpdateStudent() throws Exception {
        JSONObject studentObject = getTestStudentObject();
        studentObject.put("id", ID);
        Student student = getTestStudent(TEST);

        whenStudentIsAccessed(student);
        whenFacultyIsAccessed(FACULTY);
        when(facultyRepository.save(FACULTY)).thenReturn(FACULTY);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(RESOURCE)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faculty").value(FACULTY));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Student student = getTestStudent(TEST);

        when(avatarRepository.findByStudentId(ID)).thenReturn(Optional.of(new Avatar()));
        whenStudentIsAccessed(student);
        whenFacultyIsAccessed(FACULTY);
        when(facultyRepository.save(FACULTY)).thenReturn(FACULTY);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(RESOURCE + "/" + ID)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    public void getStudentsOfAge_shouldThrowIfThereAreZeroStudentOfThatAge() throws Exception {
        when(studentRepository.findByAge(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/search?age=" + AGE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStudentsOfAgeBetween_shouldThrowIfThereAreNoStudentsBetweenTargetAges() throws Exception {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/search-between?from=" + AGE2 + "&to=" + AGE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStudentsOfAgeBetween_shouldThrowIfGivenInvalidAge() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/search-between?from=" + INVALID_AGE + "&to=" + AGE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE + "/search-between?from=" + AGE2 + "&to=" + INVALID_AGE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetStudentsFaculty() throws Exception {
        Student student = getTestStudent(TEST);
        Faculty faculty = student.getFaculty();

        whenStudentIsAccessed(student);
        
        mockMvc.perform(MockMvcRequestBuilders
                .get(RESOURCE + "/" + ID + "/faculty")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    public void testGetAllStudents() throws Exception {
        List<Student> students = List.of(getTestStudent(TEST), getTestStudent(TEST2));

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(students.get(0)))
                .andExpect(jsonPath("$.[1]").value(students.get(1)));
    }

    @Test
    public void getAllStudents_shouldThrowIfThereIsNoStudents() throws Exception {
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private JSONObject getTestStudentObject() throws JSONException {
        JSONObject studentObject = new JSONObject();
        studentObject.put("name", TEST);
        studentObject.put("age", AGE);
        return studentObject;
    }

    private Student getTestStudent(String name) {
        Student student = new Student();
        student.setId(ID);
        student.setName(name);
        student.setAge(AGE);
        student.setFaculty(FACULTY);
        return student;
    }

    private void whenFacultyIsAccessed(Faculty faculty) {
        when(facultyRepository.existsById(eq(faculty.getId()))).thenReturn(true);
        when(facultyRepository.findById(eq(faculty.getId()))).thenReturn(Optional.of(FACULTY));
    }

    private void whenStudentIsAccessed(Student student) {
        when(studentRepository.existsById(eq(student.getId()))).thenReturn(true);
        when(studentRepository.findById(eq(student.getId()))).thenReturn(Optional.of(student));
    }
}

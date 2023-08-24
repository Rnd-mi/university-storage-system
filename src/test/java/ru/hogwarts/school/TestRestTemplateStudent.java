package ru.hogwarts.school;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hogwarts.school.constants.Constants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRestTemplateStudent {
    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    private long facultyId;

    @BeforeEach
    public void setup() {
        STUDENT.setName(TEST);
        STUDENT.setAge(AGE);
        STUDENT2.setName(TEST2);
        STUDENT2.setAge(AGE);
        createTestFaculty();
    }

    @AfterEach
    public void cleanup() {
        deleteTestFaculty();
    }

    @Test
    public void contextLoads() {
        assertNotNull(studentController);
    }

    @Test
    public void testCreateStudent() {
        Student actual = restTemplate.postForObject(getUrlWithPort() + "/" + facultyId, STUDENT, Student.class);
        assertNotNull(actual);
        assertEquals(STUDENT.getAge(), actual.getAge());
        assertEquals(STUDENT.getName(), actual.getName());

        deleteTestStudent(actual.getId());
    }

    @Test
    public void createStudent_shouldThrowIfFacultyNotPresent() {
        String answer = restTemplate.postForObject(getUrlWithPort() + "/" + ID, STUDENT, String.class);
        assertTrue(answer.contains(NOT_FOUND));
    }

    @Test
    public void testGetStudent() {
        Student expected = createTestStudent(STUDENT);
        long id = expected.getId();
        Student actual = getStudent(id);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAge(), actual.getAge());
        System.out.println(actual);
        deleteTestStudent(id);
    }

    @Test
    public void getStudent_shouldSendNotFoundMessage() {
        assertTrue(getAnswerIfGetStudent(ID).contains(NOT_FOUND));

        Student nullStudent = getStudent(ID);
        assertNull(nullStudent.getName());
        assertEquals(0, nullStudent.getAge());
    }

    @Test
    public void testUpdateStudent() {
        Student student = createTestStudent(STUDENT);
        String oldName = student.getName();
        int oldAge = student.getAge();
        student.setFaculty(null);                   // doing this because update method won't work if we are passing faculty
        student.setName(TEST2);
        student.setAge(AGE2);

        restTemplate.put(getUrlWithPort(), student);
        Student updatedStudent = getStudent(student.getId());

        assertNotEquals(oldName, updatedStudent.getName());
        assertNotEquals(oldAge, updatedStudent.getAge());
        deleteTestStudent(student.getId());
    }

    @Test
    public void testDeleteStudent() {
        Student student = createTestStudent(STUDENT);
        deleteTestStudent(student.getId());
        String actual2 = restTemplate.getForObject(getUrlWithPort() + "/" + student.getId(), String.class);
        assertTrue(actual2.contains(NOT_FOUND));
    }

    @Test
    public void testGetStudentsOfAge() {
        Student student1 = createTestStudent(STUDENT);
        Student student2 = createTestStudent(STUDENT2);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode students = restTemplate.getForObject(getUrlWithPort() + "/search?age=" + AGE, JsonNode.class);
        List<Student> studentsOfAge = mapper.convertValue(students, new TypeReference<>() {});

        for (Student student : studentsOfAge) {
            assertEquals(student.getAge(), AGE);
        }
        deleteTestStudent(student1.getId());
        deleteTestStudent(student2.getId());
    }

    @Test
    public void testGetStudentsOfAgeBetween() {
        STUDENT.setAge(AGE2);
        Student student1 = createTestStudent(STUDENT);
        Student student2 = createTestStudent(STUDENT2);

        String url = getUrlWithPort() + "/search-between?from=" + AGE2 + "&to=" + AGE;
        JsonNode students = restTemplate.getForObject(url, JsonNode.class);
        assertEquals(2, students.size());

        deleteTestStudent(student1.getId());
        deleteTestStudent(student2.getId());
    }

    @Test
    public void getStudentsOfAgeBetween_shouldThrowIfGivenInvalidAge() {
        String url = getUrlWithPort() + "/search-between?from=" + INVALID_AGE + "&to=" + AGE;
        String answer = restTemplate.getForObject(url, String.class);
        assertTrue(answer.contains(BAD_REQUEST));
    }

    @Test
    public void testGetStudentsFaculty() {
        Student student = createTestStudent(STUDENT);
        String url = getUrlWithPort() + "/" + student.getId() + "/faculty";
        Faculty faculty = restTemplate.getForObject(url, Faculty.class);
        assertEquals(facultyId, faculty.getId());

        deleteTestStudent(student.getId());
    }

    @Test
    public void testGetAllStudents() {
        Student student = createTestStudent(STUDENT);
        Student student2 = createTestStudent(STUDENT2);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode students = restTemplate.getForObject(getUrlWithPort(), JsonNode.class);
        List<Student> allStudents = mapper.convertValue(students, new TypeReference<>() {});
        assertTrue(allStudents.contains(student));
        assertTrue(allStudents.contains(student2));

        deleteTestStudent(student.getId());
        deleteTestStudent(student2.getId());
    }


    private Student getStudent(long id) {
        return restTemplate.getForObject(getUrlWithPort() + "/" + id, Student.class);
    }

    private String getAnswerIfGetStudent(long id) {
        return restTemplate.getForObject(getUrlWithPort() + "/" + id, String.class);
    }

    private Student createTestStudent(Student student) {
        return restTemplate.postForObject(getUrlWithPort() + "/" + facultyId, student, Student.class);
    }

    private void deleteTestStudent(long id) {
        restTemplate.delete(getUrlWithPort() + "/" + id);
    }

    private void createTestFaculty() {
        Faculty faculty = restTemplate.postForObject("http://localhost:" + port + "/faculties", FACULTY, Faculty.class);
        facultyId = faculty.getId();
    }

    private void deleteTestFaculty() {
        restTemplate.delete("http://localhost:" + port + "/faculties/" + facultyId, Faculty.class);
    }

    private String getUrlWithPort() {
        return "http://localhost:" + port + "/students";
    }
}

package ru.hogwarts.school;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    }

    @Test
    public void contextLoads() {
        assertNotNull(studentController);
    }

    @Test
    public void testCreateStudent() {
        createTestFaculty();
        Student actual = restTemplate.postForObject(getUrlWithPort() + "/" + facultyId, STUDENT, Student.class);
        assertNotNull(actual);
        assertEquals(STUDENT.getAge(), actual.getAge());
        assertEquals(STUDENT.getName(), actual.getName());

        deleteTestStudent(actual.getId());
        deleteTestFaculty();
    }

    @Test
    public void createStudent_shouldThrowIfFacultyNotPresent() {
        String answer = restTemplate.postForObject(getUrlWithPort() + "/" + FACULTY_ID, STUDENT, String.class);
        assertTrue(answer.contains(NOT_FOUND));
    }

    @Test
    public void testGetStudent() {
        Student expected = addTestStudent(STUDENT);
        long id = expected.getId();
        Student actual = getStudent(id);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAge(), actual.getAge());
        deleteTestStudent(id);

        Student nullStudent = getStudent(id);
        assertNull(nullStudent.getName());
    }

    @Test
    public void getStudent_shouldSendNotFoundMessage() {
        String actual = restTemplate.getForObject(getUrlWithPort() + "/" + ID, String.class);
        assertTrue(actual.contains(NOT_FOUND));
    }

    @Test
    public void testUpdateStudent() {
        createTestFaculty();
        Student student = addTestStudent(STUDENT);
        String oldName = student.getName();
        student.setName(TEST2);

        restTemplate.put(getUrlWithPort(), student);
        Student updatedStudent = getStudent(student.getId());

        assertNotEquals(oldName, updatedStudent.getName());
        deleteTestStudent(student.getId());
        deleteTestFaculty();
    }

    @Test
    public void testDeleteStudent() {
        Student student = addTestStudent(STUDENT);
        deleteTestStudent(student.getId());
        String actual = restTemplate.getForObject(getUrlWithPort() + "/" + student.getId(), String.class);
        assertTrue(actual.contains(NOT_FOUND));
    }

    @Test
    public void testGetStudentsOfAge() {
        createTestFaculty();
        Student student1 = addTestStudent(STUDENT);
        Student student2 = addTestStudent(STUDENT2);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode students = restTemplate.getForObject(getUrlWithPort() + "/search?age=" + AGE, JsonNode.class);
        List<Student> studentsOfAge = mapper.convertValue(students, new TypeReference<>() {});

        for (Student student : studentsOfAge) {
            assertEquals(student.getAge(), AGE);
        }
        deleteTestStudent(student1.getId());
        deleteTestStudent(student2.getId());
        deleteTestFaculty();
    }

    @Test
    public void testGetStudentsOfAgeBetween() {
        createTestFaculty();
        STUDENT.setAge(AGE2);
        Student student1 = addTestStudent(STUDENT);
        Student student2 = addTestStudent(STUDENT2);

        String url = getUrlWithPort() + "/search-between?from=" + AGE2 + "&to=" + AGE;
        JsonNode students = restTemplate.getForObject(url, JsonNode.class);
        assertEquals(2, students.size());

        deleteTestStudent(student1.getId());
        deleteTestStudent(student2.getId());
        deleteTestFaculty();
    }

    @Test
    public void getStudentsOfAgeBetween_shouldThrowIfGivenInvalidAge() {
        String url = getUrlWithPort() + "/search-between?from=" + INVALID_AGE + "&to=" + AGE;
        String answer = restTemplate.getForObject(url, String.class);
        assertTrue(answer.contains(INVALID_STUDENT_PROPS));
    }

    @Test
    public void testGetStudentsFaculty() {
        createTestFaculty();
        Student student = addTestStudent(STUDENT);
        String url = getUrlWithPort() + "/" + student.getId() + "/faculty";
        Faculty faculty = restTemplate.getForObject(url, Faculty.class);
        assertEquals(facultyId, faculty.getId());

        deleteTestStudent(student.getId());
        deleteTestFaculty();
    }

    @Test
    public void testGetAllStudents() {
        createTestFaculty();
        Student student = addTestStudent(STUDENT);
        Student student2 = addTestStudent(STUDENT2);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode students = restTemplate.getForObject(getUrlWithPort(), JsonNode.class);
        List<Student> allStudents = mapper.convertValue(students, new TypeReference<>() {});
        assertTrue(allStudents.contains(student));
        assertTrue(allStudents.contains(student2));

        deleteTestStudent(student.getId());
        deleteTestStudent(student2.getId());
        deleteTestFaculty();
    }


    private Student getStudent(long id) {
        return restTemplate.getForObject(getUrlWithPort() + "/" + id, Student.class);
    }

    private Student addTestStudent(Student student) {
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

package ru.hogwarts.school;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hogwarts.school.constants.Constants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestRestTemplateFaculty {
    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    private long studentId;

    @Test
    public void contextLoads() {
        assertNotNull(facultyController);
    }

    @Test
    public void testCreateFaculty() {
        Faculty actual = restTemplate.postForObject(getUrlWithPort(), FACULTY, Faculty.class);
        assertNotNull(actual);
        assertEquals(FACULTY.getColor(), actual.getColor());
        assertEquals(FACULTY.getName(), actual.getName());

        deleteFaculty(actual.getId());
    }

    @Test
    public void testCreateFaculty_shouldThrowIfInvalidInput() {
        Faculty faculty = new Faculty();
        faculty.setName(BLANK_STR);
        faculty.setColor(COLOR);
        String answer = restTemplate.postForObject(getUrlWithPort(), faculty, String.class);

        assertTrue(answer.contains(INVALID_FACULTY_PROPS));
    }

    @Test
    public void testGetFaculty() {
        Faculty expected = addTestFaculty(FACULTY);
        long id = expected.getId();
        Faculty actual = getFaculty(id);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getColor(), actual.getColor());
        deleteFaculty(id);

        Faculty nullFaculty = getFaculty(id);
        assertNull(nullFaculty.getName());
        assertNull(nullFaculty.getColor());
    }

    @Test
    public void getFaculty_shouldSendNotFoundMessage() {
        String actual = restTemplate.getForObject(getUrlWithPort() + "/" + ID, String.class);
        assertTrue(actual.contains(NOT_FOUND));
    }

    @Test
    public void testUpdateFaculty() {
        Faculty faculty = addTestFaculty(FACULTY);
        String oldName = faculty.getName();
        faculty.setName(TEST2);

        restTemplate.put(getUrlWithPort(), faculty);
        Faculty updatedFaculty = getFaculty(faculty.getId());

        assertNotEquals(oldName, updatedFaculty.getName());
        deleteFaculty(faculty.getId());
    }

    @Test
    public void testDeleteFaculty() {
        Faculty faculty = addTestFaculty(FACULTY);
        deleteFaculty(faculty.getId());
        String answer = restTemplate.getForObject(getUrlWithPort() + "/" + faculty.getId(), String.class);
        assertTrue(answer.contains(NOT_FOUND));
    }

    @Test
    public void testGetFacultiesOfColor() {
        Faculty faculty1 = addTestFaculty(FACULTY);
        Faculty faculty2 = addTestFaculty(FACULTY2);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode faculties = restTemplate.getForObject(getUrlWithPort() + "/search?color=" + COLOR, JsonNode.class);
        List<Faculty> facultiesOfColor = mapper.convertValue(faculties, new TypeReference<>() {
        });

        for (Faculty faculty : facultiesOfColor) {
            assertEquals(faculty.getColor(), COLOR);
        }
        deleteFaculty(faculty1.getId());
        deleteFaculty(faculty2.getId());
    }

    @Test
    public void testFacultiesMultiSearch() {
        Faculty faculty1 = addTestFaculty(FACULTY);
        Faculty faculty2 = addTestFaculty(FACULTY2);

        String url = getUrlWithPort() + "/multi-search?name=" + TEST;
        JsonNode faculties = restTemplate.getForObject(url, JsonNode.class);
        assertEquals(1, faculties.size());

        String url2 = getUrlWithPort() + "/multi-search?color=" + COLOR + "&name=" + TEST3;
        JsonNode faculties2 = restTemplate.getForObject(url2, JsonNode.class);
        assertEquals(2, faculties2.size());

        deleteFaculty(faculty1.getId());
        deleteFaculty(faculty2.getId());
    }

    @Test
    public void facultiesMultiSearch_shouldThrowIfThereAreNoSuchFaculties() {
        String url = getUrlWithPort() + "/multi-search";
        String answer = restTemplate.getForObject(url, String.class);
        System.out.println(answer);
        assertTrue(answer.contains(NOT_FOUND));

        String url2 = getUrlWithPort() + "/multi-search?color=" + COLOR + "&name=" + TEST3;
        String answer2 = restTemplate.getForObject(url2, String.class);
        System.out.println(answer2);
        assertTrue(answer2.contains(NOT_FOUND));
    }

    @Test
    public void testGetFacultyStudents() {
        Faculty faculty = addTestFaculty(FACULTY);
        addTestStudent(faculty.getId());

        String url = getUrlWithPort() + "/" + faculty.getId() + "/students";
        JsonNode students = restTemplate.getForObject(url, JsonNode.class);
        ObjectMapper mapper = new ObjectMapper();
        List<Student> facultyStudents = mapper.convertValue(students, new TypeReference<>() {});

        assertEquals(1, facultyStudents.size());
        assertEquals(TEST, facultyStudents.get(0).getName());
        assertEquals(AGE, facultyStudents.get(0).getAge());

        deleteTestStudent();
    }

    @Test
    public void testGetAllFaculties() {
        Faculty faculty = addTestFaculty(FACULTY);
        Faculty faculty2 = addTestFaculty(FACULTY2);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode faculties = restTemplate.getForObject(getUrlWithPort(), JsonNode.class);
        List<Faculty> allFaculties = mapper.convertValue(faculties, new TypeReference<>() {});
        assertTrue(allFaculties.contains(faculty));
        assertTrue(allFaculties.contains(faculty2));

        deleteFaculty(faculty.getId());
        deleteFaculty(faculty2.getId());
    }


    private Faculty getFaculty(long id) {
        return restTemplate.getForObject(getUrlWithPort() + "/" + id, Faculty.class);
    }

    private Faculty addTestFaculty(Faculty faculty) {
        return restTemplate.postForObject(getUrlWithPort(), faculty, Faculty.class);
    }

    private void deleteFaculty(long id) {
        restTemplate.delete(getUrlWithPort() + "/" + id);
    }

    private void addTestStudent(long facultyId) {
        STUDENT.setName(TEST);
        STUDENT.setAge(AGE);
        String url = "http://localhost:" + port + "/students/" + facultyId;
        Student student = restTemplate.postForObject(url, STUDENT, Student.class);
        studentId = student.getId();
    }

    private void deleteTestStudent() {
        restTemplate.delete("http://localhost:" + port + "/students/" + studentId, Student.class);
    }

    private String getUrlWithPort() {
        return "http://localhost:" + port + "/faculties";
    }
}

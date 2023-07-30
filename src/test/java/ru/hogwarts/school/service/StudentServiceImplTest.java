package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Student;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hogwarts.school.constants.Constants.*;
import static ru.hogwarts.school.constants.Constants.TEST2;

public class StudentServiceImplTest {
    StudentService out = new StudentServiceImpl();

    @BeforeEach
    public void setUp() {
        out.createStudent(new Student(ID, TEST, AGE));
        out.createStudent(new Student(ID, TEST, AGE));
        out.createStudent(new Student(ID, TEST, AGE));
    }

    @Test
    public void createStudent_shouldCorrectlyIncrementId() {
        Student faculty = out.createStudent(new Student(ID, TEST, AGE));
        assertEquals(4, faculty.getId());
    }

    @Test
    public void getStudent_shouldReturnNullIfStudentDoesNotExist() {
        Student faculty = out.getStudent(ID);
        assertNull(faculty);
    }

    @Test
    public void test_updateStudentInfo() {
        out.updateStudent(new Student(1, TEST2, AGE2));
        assertEquals(TEST2, out.getStudent(1).getName());
        assertEquals(AGE2, out.getStudent(1).getAge());
    }

    @Test
    public void test_deleteStudent() {
        out.deleteStudent(1);
        assertNull(out.getStudent(1));
    }

    @Test
    public void test_getFacultiesOfAge() {
        List<Student> actual = new ArrayList<>(out.getStudentsOfAge(AGE));
        assertEquals(3, actual.size());
        out.createStudent(new Student(ID, TEST2, AGE2));
        List<Student> actual2 = new ArrayList<>(out.getStudentsOfAge(AGE2));
        assertEquals(1, actual2.size());
    }
}
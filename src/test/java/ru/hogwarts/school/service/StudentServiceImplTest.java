package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.InvalidStudentPropsException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.constants.Constants.*;

public class StudentServiceImplTest {
    private StudentRepository studentRepository;
    private FacultyService facultyService;
    private StudentService out;

    @BeforeEach
    public void setup() {
        studentRepository = mock(StudentRepository.class);
        facultyService = mock(FacultyService.class);
        out = new StudentServiceImpl(studentRepository, facultyService);
        when(studentRepository.save(any(Student.class))).thenReturn(new Student(ID, TEST, AGE, FACULTY));
    }

    @Test
    public void test_createStudent() {
        Student actual = out.createStudent(new Student(ID, TEST, AGE, FACULTY), FACULTY_ID);
        Student expected = new Student(ID, TEST, AGE, FACULTY);
        assertEquals(actual, expected);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void createStudent_shouldThrowIfInvalidAge() {
        assertThrows(InvalidStudentPropsException.class, () -> out.createStudent(new Student(ID, TEST, INVALID_AGE, FACULTY), FACULTY_ID));
    }

    @Test
    public void getStudent_shouldThrowIfStudentDoesNotExist() {
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> out.getStudent(ID));
    }

    @Test
    public void test_updateStudentInfo() {
        Student test = new Student(1, TEST2, AGE, FACULTY);
        when(studentRepository.existsById(test.getId())).thenReturn(true);
        when(studentRepository.save(test)).thenReturn(test);
        when(studentRepository.findById(test.getId())).thenReturn(Optional.of(test));

        assertEquals(TEST2, out.getStudent(1).getName());
        assertEquals(AGE, out.getStudent(1).getAge());

        out.updateStudent(test);

        verify(studentRepository, times(1)).save(test);
        verify(studentRepository, times(3)).existsById(test.getId());
    }

    @Test
    public void updateStudentInfo_shouldThrowIfStudentDoesNotExist() {
        when(studentRepository.existsById(ID)).thenReturn(false);
        assertThrows(StudentNotFoundException.class, () -> out.updateStudent(new Student(ID, TEST, AGE, FACULTY)));
    }

    @Test
    public void deleteStudent_shouldThrowIfStudentDoesntExist() {
        when(studentRepository.existsById(ID)).thenReturn(false);
        assertThrows(StudentNotFoundException.class, () -> out.getStudent(ID));
    }

    @Test
    public void getStudentsOfAge_shouldThrowIfDBReturnsEmptyCollection() {
        when(studentRepository.findByAge(AGE)).thenReturn(Collections.emptyList());
        assertThrows(StudentNotFoundException.class, () -> out.getStudentsOfAge(AGE));
    }

    @Test
    public void getStudentsOfAge_shouldThrowIfAgeIsInvalid() {
        assertThrows(InvalidStudentPropsException.class, () -> out.getStudentsOfAge(INVALID_AGE));
    }

    @Test
    public void test_findAll() {
        when(studentRepository.findAll()).thenReturn(getAllStudents());
        assertEquals(3, out.getAll().size());
    }

    @Test
    public void getByAgeBetween_shouldThrowIfInvalidParam() {
        assertThrows(InvalidStudentPropsException.class, () -> out.getByAgeBetween(INVALID_AGE, AGE));
        assertThrows(InvalidStudentPropsException.class, () -> out.getByAgeBetween(AGE, INVALID_AGE));

        when(studentRepository.findByAgeBetween(AGE, AGE)).thenReturn(Collections.emptyList());
        assertThrows(StudentNotFoundException.class, () -> out.getByAgeBetween(AGE, AGE));
    }

    private List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(ID, TEST, AGE, FACULTY));
        students.add(new Student(ID, TEST, AGE, FACULTY));
        students.add(new Student(ID, TEST, AGE, FACULTY));
        return students;
    }
}

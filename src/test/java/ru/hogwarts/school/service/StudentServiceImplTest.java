package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.InvalidStudentPropsException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.constants.Constants.*;

public class StudentServiceImplTest {
    private StudentRepository studentRepository;

    private AvatarRepository avatarRepository;

    private FacultyService facultyService;

    private StudentService out;

    @BeforeEach
    public void setup() {
        studentRepository = mock(StudentRepository.class);
        avatarRepository = mock(AvatarRepository.class);
        facultyService = mock(FacultyService.class);
        out = new StudentServiceImpl(studentRepository, avatarRepository, facultyService);
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
        Student student = new Student(1, TEST2, AGE, null);
        Student studentInDb = new Student(1, TEST2, AGE, FACULTY);
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(studentInDb));

        assertEquals(TEST2, out.getStudent(1).getName());
        assertEquals(AGE, out.getStudent(1).getAge());

        out.updateStudent(student);

        verify(studentRepository, times(1)).save(student);
        verify(studentRepository, times(3)).existsById(student.getId());
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

    @Test
    public void test_getStudentsNamesThatStartsWithA() {
        List<Student> students = getAllStudents();
        students.add(new Student(ID, "Alex", AGE, FACULTY));
        when(studentRepository.findAll()).thenReturn(students);

        List<String> actual = out.getStudentsNamesThatStartsWithA().stream().toList();
        assertEquals(1, actual.size());
        assertEquals("ALEX", actual.get(0));
    }

    @Test
    public void test_computeAverageAge() {
        when(studentRepository.findAll()).thenReturn(getAllStudents());
        double expected = (AGE + AGE + AGE) / (double) 3;
        double expectedFormatted = new BigDecimal(Double.toString(expected))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        assertEquals(expectedFormatted, out.computeAverageAge());
    }

    private List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(ID, TEST, AGE, FACULTY));
        students.add(new Student(ID, TEST, AGE, FACULTY));
        students.add(new Student(ID, TEST, AGE, FACULTY));
        return students;
    }
}

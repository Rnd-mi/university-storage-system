package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.InvalidFacultyPropsException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.hogwarts.school.constants.Constants.*;

public class
FacultyServiceImplTest {
    private FacultyRepository facultyRepository;

    private StudentRepository studentRepository;

    private FacultyService out;

    @BeforeEach
    public void setUp() {
        facultyRepository = mock(FacultyRepository.class);
        studentRepository = mock(StudentRepository.class);
        out = new FacultyServiceImpl(facultyRepository, studentRepository);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(new Faculty(ID, TEST, TEST));
    }

    @Test
    public void test_createFaculty() {
        Faculty actual = out.createFaculty(new Faculty(ID, TEST, TEST));
        Faculty expected = new Faculty(ID, TEST, TEST);
        assertEquals(actual, expected);
        verify(facultyRepository, times(1)).save(any(Faculty.class));
    }

    @Test
    public void createFaculty_shouldThrowIfInvalidColor() {
        assertThrows(InvalidFacultyPropsException.class, () -> out.createFaculty(new Faculty(ID, TEST, BLANK_STR)));
    }

    @Test
    public void getFaculty_shouldThrowIfFacultyDoesNotExist() {
        when(facultyRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(FacultyNotFoundException.class, () -> out.getFaculty(ID));
    }

    @Test
    public void test_updateFacultyInfo() {
        Faculty test = new Faculty(1, TEST2, TEST2);
        when(facultyRepository.existsById(test.getId())).thenReturn(true);
        when(facultyRepository.save(test)).thenReturn(test);
        when(facultyRepository.findById(test.getId())).thenReturn(Optional.of(test));

        assertEquals(TEST2, out.getFaculty(1).getName());
        assertEquals(TEST2, out.getFaculty(1).getColor());

        out.updateFaculty(test);

        verify(facultyRepository, times(1)).save(test);
        verify(facultyRepository, times(3)).existsById(test.getId());
    }

    @Test
    public void updateFacultyInfo_shouldThrowIfFacultyDoesNotExist() {
        when(facultyRepository.existsById(ID)).thenReturn(false);
        assertThrows(FacultyNotFoundException.class, () -> out.updateFaculty(new Faculty(ID, TEST, COLOR)));
    }

    @Test
    public void deleteFaculty_shouldThrowIfFacultyDoesntExist() {
        when(facultyRepository.existsById(ID)).thenReturn(false);
        assertThrows(FacultyNotFoundException.class, () -> out.getFaculty(ID));
    }

    @Test
    public void getFacultiesOfColor_shouldThrowIfDBReturnsEmptyCollection() {
        when(facultyRepository.findByColorIgnoreCase(COLOR)).thenReturn(Collections.emptyList());
        assertThrows(FacultyNotFoundException.class, () -> out.getFacultiesOfColor(COLOR));
    }

    @Test
    public void getFacultiesOfColor_shouldThrowIfColorIsInvalid() {
        assertThrows(FacultyNotFoundException.class, () -> out.getFacultiesOfColor(BLANK_STR));
    }

    @Test
    public void test_findAll() {
        when(facultyRepository.findAll()).thenReturn(getAllFaculties());
        assertEquals(3, out.getAll().size());
    }

    @Test
    public void getFacultyByColorOrName_shouldThrowIfInvalidParam() {
        when(facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(TEST, TEST)).thenReturn(Collections.emptyList());
        assertThrows(FacultyNotFoundException.class, () -> out.getFacultyByColorOrName(TEST, TEST));
    }

    @Test
    public void test_getLongestFacultyName() {
        when(facultyRepository.findAll()).thenReturn(getAllFaculties());
        assertEquals(TEST, out.getLongestFacultyName());
    }

    @Test
    public void getLongestFacultyName_shouldThrowIfEmptyRepository() {
        assertThrows(FacultyNotFoundException.class, () -> out.getLongestFacultyName());
    }

    private List<Faculty> getAllFaculties() {
        List<Faculty> faculties = new ArrayList<>();
        faculties.add(new Faculty(ID, TEST, TEST));
        faculties.add(new Faculty(ID, TEST, TEST));
        faculties.add(new Faculty(ID, TEST, TEST));
        return faculties;
    }
}

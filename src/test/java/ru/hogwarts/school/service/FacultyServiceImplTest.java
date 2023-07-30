package ru.hogwarts.school.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hogwarts.school.constants.Constants.*;

public class FacultyServiceImplTest {
    FacultyService out = new FacultyServiceImpl();

    @BeforeEach
    public void setUp() {
        out.createFaculty(new Faculty(ID, TEST, TEST));
        out.createFaculty(new Faculty(ID, TEST, TEST));
        out.createFaculty(new Faculty(ID, TEST, TEST));
    }

    @Test
    public void createFaculty_shouldCorrectlyIncrementId() {
        Faculty faculty = out.createFaculty(new Faculty(ID, TEST, TEST));
        assertEquals(4, faculty.getId());
    }

    @Test
    public void getFaculty_shouldReturnNullIfFacultyDoesNotExist() {
        Faculty faculty = out.getFaculty(ID);
        assertNull(faculty);
    }

    @Test
    public void test_updateFacultyInfo() {
        out.updateFaculty(new Faculty(1, TEST2, TEST2));
        assertEquals(TEST2, out.getFaculty(1).getName());
        assertEquals(TEST2, out.getFaculty(1).getColor());
    }

    @Test
    public void test_deleteFaculty() {
        out.deleteFaculty(1);
        assertNull(out.getFaculty(1));
    }

    @Test
    public void test_getFacultiesOfColor() {
        List<Faculty> actual = new ArrayList<>(out.getFacultiesOfColor(TEST));
        assertEquals(3, actual.size());
        out.createFaculty(new Faculty(ID, TEST2, TEST2));
        List<Faculty> actual2 = new ArrayList<>(out.getFacultiesOfColor(TEST2));
        assertEquals(1, actual2.size());
    }
}
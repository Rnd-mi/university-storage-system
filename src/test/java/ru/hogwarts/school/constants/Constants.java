package ru.hogwarts.school.constants;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

public class Constants {
    public final static String TEST = "test";
    public final static String TEST2 = "test2";
    public final static String TEST3 = "test3";
    public final static long ID = 10;
    public final static int AGE = 15;
    public final static int AGE2 = 11;
    public final static long FACULTY_ID = 13;
    public final static int INVALID_AGE = 5;
    public final static String COLOR = "yellow";
    public final static String BLANK_STR = "    ";
    public final static Faculty FACULTY = new Faculty(FACULTY_ID, TEST, COLOR);
    public final static Faculty FACULTY2 = new Faculty(FACULTY_ID, TEST2, COLOR);
    public final static Student STUDENT = new Student();
    public final static Student STUDENT2 = new Student();
    public final static String NOT_FOUND = "Not Found";
    public final static String INVALID_STUDENT_PROPS = "2. 'age' should be in range of 7 to 20";
    public final static String INVALID_FACULTY_PROPS = "'name' and 'color' must contains characters";
}

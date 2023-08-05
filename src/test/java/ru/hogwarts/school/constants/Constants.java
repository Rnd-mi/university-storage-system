package ru.hogwarts.school.constants;

import ru.hogwarts.school.model.Faculty;

public class Constants {
    public final static String TEST = "test";
    public final static String TEST2 = "test2";
    public final static long ID = 10;
    public final static int AGE = 15;
    public final static long FACULTY_ID = 2;
    public final static int INVALID_AGE = 5;
    public final static String YELLOW = "yellow";
    public final static String BLANK_STR = "    ";
    public final static Faculty FACULTY = new Faculty(FACULTY_ID, TEST, YELLOW);
}

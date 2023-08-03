package ru.hogwarts.school.utility;

import ru.hogwarts.school.exception.InvalidFacultyPropsException;
import ru.hogwarts.school.exception.InvalidStudentPropsException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

public class InputValidator {
    public static void validateAge(int age) {
        if (age < 7 || age > 20) {
            throw new InvalidStudentPropsException();
        }
    }

    public static void validateStudentProps(Student student) {
        String name = student.getName();
        int age = student.getAge();

        if (name == null || name.isBlank()) {
            throw new InvalidStudentPropsException();
        }
        validateAge(age);
    }

    public static void validateFacultyProps(Faculty faculty) {
        String name = faculty.getName();
        String color = faculty.getColor();

        if (name == null || name.isBlank() || color == null || color.isBlank()) {
            throw new InvalidFacultyPropsException();
        }
    }
}

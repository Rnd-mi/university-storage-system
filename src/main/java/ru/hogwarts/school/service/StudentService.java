package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentService {

    Student createStudent(Student student, long facultyId);

    Student getStudent(long id);

    void deleteStudent(long id);

    Student updateStudent(Student student);

    Collection<Student> getStudentsOfAge(int age);

    Collection<Student> getAll();

    Collection<Student> getByAgeBetween(int from, int to);

    void printNamesInConsoleInOrder();

    Faculty getFaculty(long id);

    long getNumberOfStudents();

    long getAverageAge();

    Collection<Student> getLastFiveStudents();

    Collection<String> getStudentsNamesThatStartsWithA();

    double computeAverageAge();

    void printNamesInConsole();
}

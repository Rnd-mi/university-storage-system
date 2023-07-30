package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentService {
    Student createStudent(Student student);

    Student getStudent(long id);

    Student deleteStudent(long id);

    Student updateStudent(Student student);

    Collection<Student> getStudentsOfAge(int age);
}

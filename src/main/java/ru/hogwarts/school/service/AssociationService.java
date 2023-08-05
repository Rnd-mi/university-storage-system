package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface AssociationService {
    Student changeFacultyForStudent(long StudentId, long facultyId);

    Collection<Student> changeStudentsInFaculty(long facultyId, List<Long> idList);
}

package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Faculty getFaculty(long id);

    void deleteFaculty(long id);

    Faculty updateFaculty(Faculty faculty);

    Collection<Faculty> getFacultiesOfColor(String color);

    Collection<Faculty> getAll();
}

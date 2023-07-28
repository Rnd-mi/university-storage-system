package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long lastId = 0;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++lastId);
        faculties.put(lastId, faculty);
        return faculty;
    }

    public Faculty getFaculty(long id) {
        return faculties.get(id);
    }

    public Faculty updateFacultyInfo(Faculty faculty) {
        if (faculties.containsKey(faculty.getId())) {
            return faculties.put(faculty.getId(), faculty);
        }
        return null;
    }

    public Faculty deleteFaculty(long id) {
        return faculties.remove(id);
    }
}

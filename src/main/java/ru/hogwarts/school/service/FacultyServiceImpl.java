package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.BadColorException;
import ru.hogwarts.school.exception.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        checkColor(faculty.getColor());
        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            throw new FacultyAlreadyExistsException();
        }
    }

    public Faculty getFaculty(long id) {
        checkIfExist(id);
        return facultyRepository.findById(id).get();
    }

    public Faculty updateFaculty(Faculty faculty) {
        checkIfExist(faculty.getId());
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        checkIfExist(id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getFacultiesOfColor(String color) {
        checkColor(color);
        Collection<Faculty> faculties = facultyRepository.findByColor(color);

        if (faculties.isEmpty()) {
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    public Collection<Faculty> getAll() {
        Collection<Faculty> faculties = facultyRepository.findAll();

        if (faculties.isEmpty()) {
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    private void checkColor(String color) {
        if (color == null || color.isBlank()) {
            throw new BadColorException();
        }
    }

    private void checkIfExist(long id) {
        if (!facultyRepository.existsById(id)) {
            throw new FacultyNotFoundException();
        }
    }
}

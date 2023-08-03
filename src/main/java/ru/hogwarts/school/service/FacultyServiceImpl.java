package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

import static ru.hogwarts.school.utility.InputValidator.validateFacultyProps;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        validateFacultyProps(faculty);
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
        validateFacultyProps(faculty);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        checkIfExist(id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getFacultiesOfColor(String color) {
        Collection<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);

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

    @Override
    public Collection<Faculty> getFacultyByColorOrName(String color, String name) {
        Collection<Faculty> result = facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(color, name);

        if (result.isEmpty()) {
            throw new FacultyNotFoundException();
        }
        return result;
    }

    private void checkIfExist(long id) {
        if (!facultyRepository.existsById(id)) {
            throw new FacultyNotFoundException();
        }
    }
}

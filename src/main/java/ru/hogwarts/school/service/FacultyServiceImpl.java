package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.validateFacultyProps;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    private final StudentRepository studentRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository,
                              StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        validateFacultyProps(faculty);
        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            throw new FacultyAlreadyExistsException();
        }
    }

    @Override
    public Faculty getFaculty(long id) {
        checkIfExist(id);
        return facultyRepository.findById(id).get();
    }

    @Override
    public Faculty updateFaculty(Faculty faculty) {
        checkIfExist(faculty.getId());
        return createFaculty(faculty);
    }

    @Override
    public void deleteFaculty(long id) {
        checkIfExist(id);
        List<Student> students = studentRepository.findStudentsByFacultyId(id);

        for (Student student : students) {
            student.setFaculty(null);
            studentRepository.save(student);
        }
        facultyRepository.deleteById(id);
    }

    @Override
    public Collection<Faculty> getFacultiesOfColor(String color) {
        Collection<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);

        if (faculties.isEmpty()) {
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    @Override
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

    @Override
    public Collection<Student> getStudents(long id) {
        checkIfExist(id);
        Faculty faculty = facultyRepository.findById(id).get();
        return faculty.getStudents();
    }

    public void checkIfExist(long id) {
        if (!facultyRepository.existsById(id)) {
            throw new FacultyNotFoundException();
        }
    }
}

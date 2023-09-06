package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.validateFacultyProps;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    private final StudentRepository studentRepository;

    private final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    public FacultyServiceImpl(FacultyRepository facultyRepository,
                              StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        logThatMethodInvoked("createFaculty");
        validateFacultyProps(faculty);
        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            logger.error("Attempt to create faculty which is already in repo. {}", faculty);
            throw new FacultyAlreadyExistsException();
        }
    }

    @Override
    public Faculty getFaculty(long id) {
        logThatMethodInvoked("getFaculty");
        checkIfExist(id);
        return facultyRepository.findById(id).get();
    }

    @Override
    public Faculty updateFaculty(Faculty faculty) {
        logThatMethodInvoked("updateFaculty");
        checkIfExist(faculty.getId());
        return createFaculty(faculty);
    }

    @Override
    public void deleteFaculty(long id) {
        logThatMethodInvoked("deleteFaculty");
        checkIfExist(id);
        List<Student> students = studentRepository.findStudentsByFacultyId(id);

        logger.debug("Set 'faculty' field of students in faculty to null. Faculty id = {}", id);
        for (Student student : students) {
            student.setFaculty(null);
            studentRepository.save(student);
        }
        facultyRepository.deleteById(id);
    }

    @Override
    public Collection<Faculty> getFacultiesOfColor(String color) {
        logThatMethodInvoked("getFacultiesOfColor");
        Collection<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);

        if (faculties.isEmpty()) {
            logger.error("There are no faculties of color = {}", color);
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    @Override
    public Collection<Faculty> getAll() {
        logThatMethodInvoked("getAll");
        Collection<Faculty> faculties = facultyRepository.findAll();

        if (faculties.isEmpty()) {
            logger.error("Repository of faculties is empty");
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    @Override
    public Collection<Faculty> getFacultyByColorOrName(String color, String name) {
        logThatMethodInvoked("getFacultyByColorOrName");
        Collection<Faculty> result = facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(color, name);

        if (result.isEmpty()) {
            logger.error("There are no faculties with color = {} or name = {}", color, name);
            throw new FacultyNotFoundException();
        }
        return result;
    }

    @Override
    public Collection<Student> getStudents(long id) {
        logThatMethodInvoked("getStudents");
        checkIfExist(id);
        Faculty faculty = facultyRepository.findById(id).get();
        return faculty.getStudents();
    }

    @Override
    public String getLongestFacultyName() {
        logThatMethodInvoked("getLongestFacultyName");
        return facultyRepository.findAll().stream()
                .map(el -> el.getName())
                .max(Comparator.comparingInt(el -> el.length()))
                .orElseThrow(() -> new FacultyNotFoundException());
    }

    public void checkIfExist(long id) {
        if (!facultyRepository.existsById(id)) {
            logger.error("Faculty with id = {} doesn't exist", id);
            throw new FacultyNotFoundException();
        }
    }

    private void logThatMethodInvoked(String methodName) {
        logger.info("Method {} was invoked", methodName);
    }
}

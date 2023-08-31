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
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.validateFacultyProps;
import static ru.hogwarts.school.utility.MessageGenerator.*;

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
        logger.info(generateMsgIfMethodInvoked("createFaculty"));
        validateFacultyProps(faculty);
        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            logger.error(generateMsgWhenException(getAlreadyExistsException()));
            throw new FacultyAlreadyExistsException();
        }
    }

    @Override
    public Faculty getFaculty(long id) {
        logger.info(generateMsgIfMethodInvoked("getFaculty"));
        checkIfExist(id);
        return facultyRepository.findById(id).get();
    }

    @Override
    public Faculty updateFaculty(Faculty faculty) {
        logger.info(generateMsgIfMethodInvoked("updateFaculty"));
        checkIfExist(faculty.getId());
        return createFaculty(faculty);
    }

    @Override
    public void deleteFaculty(long id) {
        logger.info(generateMsgIfMethodInvoked("deleteFaculty"));
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
        logger.info(generateMsgIfMethodInvoked("getFacultiesOfColor"));
        Collection<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);

        if (faculties.isEmpty()) {
            logger.error(generateMsgWhenException(getNotFoundException()));
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    @Override
    public Collection<Faculty> getAll() {
        logger.info(generateMsgIfMethodInvoked("getAll"));
        Collection<Faculty> faculties = facultyRepository.findAll();

        if (faculties.isEmpty()) {
            logger.error(generateMsgWhenException(getNotFoundException()));
            throw new FacultyNotFoundException();
        }
        return faculties;
    }

    @Override
    public Collection<Faculty> getFacultyByColorOrName(String color, String name) {
        logger.info(generateMsgIfMethodInvoked("getFacultyByColorOrName"));
        Collection<Faculty> result = facultyRepository.findByColorIgnoreCaseOrNameIgnoreCase(color, name);

        if (result.isEmpty()) {
            logger.error(generateMsgWhenException(getNotFoundException()));
            throw new FacultyNotFoundException();
        }
        return result;
    }

    @Override
    public Collection<Student> getStudents(long id) {
        logger.info(generateMsgWhenException("getStudents"));
        checkIfExist(id);
        Faculty faculty = facultyRepository.findById(id).get();
        return faculty.getStudents();
    }

    public void checkIfExist(long id) {
        logger.debug(generateMsgIfMethodInvoked("checkIfExist"));
        if (!facultyRepository.existsById(id)) {
            logger.error(generateMsgWhenException(getNotFoundException(), id));
            throw new FacultyNotFoundException();
        }
    }

    private String getNotFoundException() {
        return "FacultyNotFoundException";
    }

    private String getAlreadyExistsException() {
        return "FacultyAlreadyExistsException";
    }
}

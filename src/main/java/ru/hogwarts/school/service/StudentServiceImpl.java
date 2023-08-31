package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EditOrChangeFacultyPermissionException;
import ru.hogwarts.school.exception.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.utility.MessageGenerator;

import java.util.Collection;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.validateAge;
import static ru.hogwarts.school.utility.InputValidator.validateStudentProps;
import static ru.hogwarts.school.utility.MessageGenerator.*;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    private final AvatarRepository avatarRepository;

    private final FacultyService facultyService;

    private final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    public StudentServiceImpl(StudentRepository studentRepository,
                              AvatarRepository avatarRepository,
                              FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
        this.facultyService = facultyService;
    }

    @Override
    public Student createStudent(Student student, long facultyId) {
        logger.info(generateMsgIfMethodInvoked("createStudent"));
        validateStudentProps(student);
        Faculty faculty = facultyService.getFaculty(facultyId);
        student.setFaculty(faculty);
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            logger.error(MessageGenerator.generateMsgWhenException(getAlreadyExistsException(), student));
            throw new StudentAlreadyExistsException();
        }
    }

    @Override
    public Student getStudent(long id) {
        logger.info(generateMsgIfMethodInvoked("getStudent"));
        return checkIfExist(id);
    }

    @Override
    public Student updateStudent(Student student) {
        logger.info(generateMsgIfMethodInvoked("updateStudent"));
        Student studentInDb = checkIfExist(student.getId());

        if (student.getFaculty() != null) {
            logger.error(generateMsgWhenException(getPermissionException()));
            throw new EditOrChangeFacultyPermissionException();
        }

        return createStudent(student, studentInDb.getFaculty().getId());
    }

    @Override
    public void deleteStudent(long id) {
        logger.info(generateMsgIfMethodInvoked("deleteStudent"));
        Student student = getStudent(id);
        deleteAvatarIfExist(id);
        Faculty faculty = student.getFaculty();
        faculty.expelStudent(student);
        facultyService.updateFaculty(faculty);

        studentRepository.deleteById(id);
    }

    @Override
    public Collection<Student> getStudentsOfAge(int age) {
        logger.info(generateMsgIfMethodInvoked("getStudentsOfAge"));
        validateAge(age);
        Collection<Student> students = studentRepository.findByAge(age);

        if (students.isEmpty()) {
            logger.error(generateMsgWhenException(getNotFoundException()));
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getAll() {
        logger.info(generateMsgIfMethodInvoked("getAll"));
        Collection<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            logger.error(generateMsgWhenException(getNotFoundException()));
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getByAgeBetween(int from, int to) {
        logger.info(generateMsgIfMethodInvoked("getByAgeBetween"));
        validateAge(from);
        validateAge(to);

        List<Student> result = studentRepository.findByAgeBetween(from, to);
        if (result.isEmpty()) {
            logger.error(generateMsgWhenException(getNotFoundException()));
            throw new StudentNotFoundException();
        }
        return result;
    }

    @Override
    public long getNumberOfStudents() {
        logger.info(generateMsgIfMethodInvoked("getNumberOfStudents"));
        return studentRepository.countAllStudents();
    }

    @Override
    public long getAverageAge() {
        logger.info(generateMsgIfMethodInvoked("getAverageAge"));
        return studentRepository.getAverageAge();
    }

    @Override
    public List<Student> getLastFiveStudents() {
        logger.info(generateMsgIfMethodInvoked("getLastFiveStudents"));
        return studentRepository.findLastFiveStudents();
    }

    @Override
    public Faculty getFaculty(long id) {
        logger.info(generateMsgIfMethodInvoked("getFaculty"));
        Student student = getStudent(id);
        return student.getFaculty();
    }

    private Student checkIfExist(long id) {
        logger.debug(generateMsgIfMethodInvoked("checkIfExist"));
        if (!studentRepository.existsById(id)) {
            logger.error(MessageGenerator.generateMsgWhenException(getNotFoundException(), id));
            throw new StudentNotFoundException();
        }
        return studentRepository.findById(id).get();
    }

    private void deleteAvatarIfExist(long studentId) {
        logger.debug(generateMsgIfMethodInvoked("deleteAvatar"));
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);
        if (!(avatar == null)) {
            avatarRepository.delete(avatar);
        }
    }

    private String getNotFoundException() {
        return "StudentNotFoundException";
    }

    private String getAlreadyExistsException() {
        return "StudentAlreadyExistsException";
    }

    private String getPermissionException() {
        return "EditOrChangeFacultyPermissionException";
    }
}

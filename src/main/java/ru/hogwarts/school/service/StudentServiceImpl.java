package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EditOrChangeFacultyPermissionException;
import ru.hogwarts.school.exception.StudentAlreadyExists;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.*;
import static ru.hogwarts.school.utility.MessageGenerator.getMsgIfMethodInvoked;

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
        logger.info(getMsgIfMethodInvoked("createStudent"));
        validateStudentProps(student);
        Faculty faculty = facultyService.getFaculty(facultyId);
        student.setFaculty(faculty);
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            throw new StudentAlreadyExists();
        }
    }

    @Override
    public Student getStudent(long id) {
        logger.info(getMsgIfMethodInvoked("getStudent"));
        return checkIfExist(id);
    }

    @Override
    public Student updateStudent(Student student) {
        logger.info(getMsgIfMethodInvoked("updateStudent"));
        Student studentInDb = checkIfExist(student.getId());

        if (student.getFaculty() != null) {
            throw new EditOrChangeFacultyPermissionException();
        }

        return createStudent(student, studentInDb.getFaculty().getId());
    }

    @Override
    public void deleteStudent(long id) {
        logger.info(getMsgIfMethodInvoked("deleteStudent"));
        deleteAvatar(id);
        Student student = getStudent(id);
        Faculty faculty = student.getFaculty();
        faculty.expelStudent(student);
        facultyService.updateFaculty(faculty);

        studentRepository.deleteById(id);
    }

    @Override
    public Collection<Student> getStudentsOfAge(int age) {
        logger.info(getMsgIfMethodInvoked("getStudentsOfAge"));
        validateAge(age);
        Collection<Student> students = studentRepository.findByAge(age);

        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getAll() {
        logger.info(getMsgIfMethodInvoked("getAll"));
        Collection<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getByAgeBetween(int from, int to) {
        logger.info(getMsgIfMethodInvoked("getByAgeBetween"));
        validateAge(from);
        validateAge(to);

        List<Student> result = studentRepository.findByAgeBetween(from, to);
        if (result.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return result;
    }

    @Override
    public long getNumberOfStudents() {
        logger.info(getMsgIfMethodInvoked("getNumberOfStudents"));
        return studentRepository.countAllStudents();
    }

    @Override
    public long getAverageAge() {
        logger.info(getMsgIfMethodInvoked("getAverageAge"));
        return studentRepository.getAverageAge();
    }

    @Override
    public List<Student> getLastFiveStudents() {
        logger.info(getMsgIfMethodInvoked("getLastFiveStudents"));
        return studentRepository.findLastFiveStudents();
    }

    @Override
    public Faculty getFaculty(long id) {
        logger.info(getMsgIfMethodInvoked("getFaculty"));
        Student student = getStudent(id);
        return student.getFaculty();
    }

    private Student checkIfExist(long id) {
        logger.info(getMsgIfMethodInvoked("checkIfExist"));
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException();
        }
        return studentRepository.findById(id).get();
    }

    private void deleteAvatar(long studentId) {
        logger.info(getMsgIfMethodInvoked("deleteAvatar"));
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);
        if (!(avatar == null)) {
            avatarRepository.delete(avatar);
        }
    }
}

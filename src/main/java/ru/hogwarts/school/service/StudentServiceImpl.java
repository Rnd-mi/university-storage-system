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

import java.util.Collection;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.validateAge;
import static ru.hogwarts.school.utility.InputValidator.validateStudentProps;

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
        logThatMethodInvoked("createStudent");
        validateStudentProps(student);
        Faculty faculty = facultyService.getFaculty(facultyId);
        student.setFaculty(faculty);
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            logger.error("Attempt to create student which already in repo. {}", student);
            throw new StudentAlreadyExistsException();
        }
    }

    @Override
    public Student getStudent(long id) {
        logThatMethodInvoked("getStudent");
        return checkIfExist(id);
    }

    @Override
    public Student updateStudent(Student student) {
        logThatMethodInvoked("updateStudent");
        Student studentInDb = checkIfExist(student.getId());

        if (student.getFaculty() != null) {
            logger.error("User was trying to pass not null faculty, " +
                    "and possibly had a purpose to change student's faculty or edit it");
            throw new EditOrChangeFacultyPermissionException();
        }

        return createStudent(student, studentInDb.getFaculty().getId());
    }

    @Override
    public void deleteStudent(long id) {
        logThatMethodInvoked("deleteStudent");
        Student student = getStudent(id);
        deleteAvatarIfExist(id);
        Faculty faculty = student.getFaculty();
        logger.debug("Expelling student '{}' from faculty '{}'", student, faculty);
        faculty.expelStudent(student);
        facultyService.updateFaculty(faculty);

        studentRepository.deleteById(id);
    }

    @Override
    public Collection<Student> getStudentsOfAge(int age) {
        logThatMethodInvoked("getStudentsOfAge");
        validateAge(age);
        Collection<Student> students = studentRepository.findByAge(age);

        if (students.isEmpty()) {
            logger.error("There are no students of age = {}", age);
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getAll() {
        logThatMethodInvoked("getAll");
        Collection<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            logger.error("Repository of students is empty");
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getByAgeBetween(int from, int to) {
        logThatMethodInvoked("getByAgeBetween");
        validateAge(from);
        validateAge(to);

        List<Student> result = studentRepository.findByAgeBetween(from, to);
        if (result.isEmpty()) {
            logger.error("There are no students of age from {} to {}", from, to);
            throw new StudentNotFoundException();
        }
        return result;
    }

    @Override
    public long getNumberOfStudents() {
        logThatMethodInvoked("getNumberOfStudents");
        return studentRepository.countAllStudents();
    }

    @Override
    public long getAverageAge() {
        logThatMethodInvoked("getAverageAge");
        return studentRepository.getAverageAge();
    }

    @Override
    public List<Student> getLastFiveStudents() {
        logThatMethodInvoked("getLastFiveStudents");
        return studentRepository.findLastFiveStudents();
    }

    @Override
    public Collection<String> getStudentsNamesThatStartsWithA() {
        logThatMethodInvoked("getStudentsNamesThatStartsWithA");
        return studentRepository.findAll().stream()
                .parallel()
                .filter(el -> el.getName().startsWith("A"))
                .map(el -> el.getName().toUpperCase())
                .sorted()
                .toList();
    }

    @Override
    public double computeAverageAge() {
        logThatMethodInvoked("computeAverageAge");
        return studentRepository.findAll().stream()
                .mapToInt(el -> el.getAge())
                .summaryStatistics()
                .getAverage();
    }

    @Override
    public Faculty getFaculty(long id) {
        logThatMethodInvoked("getFaculty");
        Student student = getStudent(id);
        return student.getFaculty();
    }

    private Student checkIfExist(long id) {
        if (!studentRepository.existsById(id)) {
            logger.error("Student with id = {} doesn't exist", id);
            throw new StudentNotFoundException();
        }
        return studentRepository.findById(id).get();
    }

    private void deleteAvatarIfExist(long studentId) {
        logThatMethodInvoked("deleteAvatarIfExist");
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);
        if (!(avatar == null)) {
            avatarRepository.delete(avatar);
        }
    }

    private void logThatMethodInvoked(String methodName) {
        logger.info("Method {} was invoked", methodName);
    }
}

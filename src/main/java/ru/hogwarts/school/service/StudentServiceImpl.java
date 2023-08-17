package ru.hogwarts.school.service;

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

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    private final AvatarRepository avatarRepository;

    private final FacultyService facultyService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              AvatarRepository avatarRepository,
                              FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
        this.facultyService = facultyService;
    }

    @Override
    public Student createStudent(Student student, long facultyId) {
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
        return checkIfExist(id);
    }

    @Override
    public Student updateStudent(Student student) {
        Student studentInDb = checkIfExist(student.getId());

        if (student.getFaculty() != null) {
            throw new EditOrChangeFacultyPermissionException();
        }

        return createStudent(student, studentInDb.getFaculty().getId());
    }

    @Override
    public void deleteStudent(long id) {
        deleteAvatar(id);
        Student student = getStudent(id);
        Faculty faculty = student.getFaculty();
        faculty.expelStudent(student);
        facultyService.updateFaculty(faculty);

        studentRepository.deleteById(id);
    }

    @Override
    public Collection<Student> getStudentsOfAge(int age) {
        validateAge(age);
        Collection<Student> students = studentRepository.findByAge(age);

        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getAll() {
        Collection<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    @Override
    public Collection<Student> getByAgeBetween(int from, int to) {
        validateAge(from);
        validateAge(to);

        List<Student> result = studentRepository.findByAgeBetween(from, to);
        if (result.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return result;
    }

    @Override
    public Faculty getFaculty(long id) {
        Student student = getStudent(id);
        return student.getFaculty();
    }

    private Student checkIfExist(long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException();
        }
        return studentRepository.findById(id).get();
    }

    private void deleteAvatar(long studentId) {
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(null);
        if (!(avatar == null)) {
            avatarRepository.delete(avatar);
        }
    }
}

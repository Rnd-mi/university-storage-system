package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentAlreadyExists;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.validateAge;
import static ru.hogwarts.school.utility.InputValidator.validateStudentProps;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        validateStudentProps(student);
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            throw new StudentAlreadyExists();
        }
    }

    public Student getStudent(long id) {
        checkIfExist(id);
        return studentRepository.findById(id).get();
    }

    public Student updateStudent(Student student) {
        checkIfExist(student.getId());
        validateStudentProps(student);
        return createStudent(student);
    }

    public void deleteStudent(long id) {
        checkIfExist(id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> getStudentsOfAge(int age) {
        validateAge(age);
        Collection<Student> students = studentRepository.findByAge(age);

        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    public Collection<Student> getAll() {
        Collection<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    public Collection<Student> getByAgeBetween(int from, int to) {
        validateAge(from);
        validateAge(to);

        List<Student> result = studentRepository.findByAgeBetween(from, to);
        if (result.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return result;
    }

    private void checkIfExist(long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException();
        }
    }
}

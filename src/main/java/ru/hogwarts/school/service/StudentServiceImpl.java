package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.BadAgeException;
import ru.hogwarts.school.exception.StudentAlreadyExists;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        checkAge(student.getAge());
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
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        checkIfExist(id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> getStudentsOfAge(int age) {
        checkAge(age);
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

    private void checkIfExist(long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException();
        }
    }

    private void checkAge(int age) {
        if (age < 7 || age > 18) {
            throw new BadAgeException();
        }
    }
}

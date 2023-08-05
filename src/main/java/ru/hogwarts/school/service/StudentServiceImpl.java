package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentAlreadyExists;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

import static ru.hogwarts.school.utility.InputValidator.*;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final FacultyService facultyService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              FacultyService facultyService) {
        this.studentRepository = studentRepository;
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
        checkIfExist(id);
        return studentRepository.findById(id).get();
    }

    @Override
    public Student updateStudent(Student student) {
        checkIfExist(student.getId());
        facultyService.createFaculty(student.getFaculty());
        return createStudent(student, student.getFaculty().getId());
    }

    @Override
    public void deleteStudent(long id) {
        checkIfExist(id);
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

    private void checkIfExist(long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException();
        }
    }
}
